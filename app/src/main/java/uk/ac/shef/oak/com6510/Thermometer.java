/*
 * Copyright (c) 2019. This code has been developed by Fabio Ciravegna, The University of Sheffield. All rights reserved. No part of this code can be used without the explicit written permission by the author
 */

package uk.ac.shef.oak.com6510;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;
public class Thermometer {
    private static final String TAG = Thermometer.class.getSimpleName();
    private long mSamplingRateInMSecs;
    private long mSamplingRateNano;
    private SensorEventListener mTemperatureListener = null;
    private SensorManager mSensorManager;
    private Sensor mThermometerSensor;
    private long timePhoneWasLastRebooted = 0;
    private long lastReportTime = 0;
    private long THERMOMETER_READING_FREQUENCY= 3000;
    private boolean started;
    private float currentTemperatureValue;

    public Thermometer(Context context) {

        timePhoneWasLastRebooted = System.currentTimeMillis() - SystemClock.elapsedRealtime();
        mSamplingRateNano = (long) (THERMOMETER_READING_FREQUENCY) * 1000000;
        mSamplingRateInMSecs = (long) THERMOMETER_READING_FREQUENCY;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mThermometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        initThermometerListener();

    }

    private void initThermometerListener() {
        if (!standardTemperatureSensorAvailable()) {
            Log.d(TAG, "Standard Thermometer unavailable");
        } else {
            Log.d(TAG, "Using Thermometer");
            mTemperatureListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    long diff = event.timestamp - lastReportTime;

                    // time is in nanoseconds it represents the set reference times the first time we come here
                    // set event timestamp to current time in milliseconds
                    // see answer 2 at http://stackoverflow.com/questions/5500765/accelerometer-sensorevent-timestamp
                    // the following operation avoids reporting too many events too quickly - the sensor may always
                    // misbehave and start sending data very quickly
                    if (diff >= mSamplingRateNano) {
                        long actualTimeInMseconds = timePhoneWasLastRebooted + (long) (event.timestamp / 1000000.0);
                        currentTemperatureValue = event.values[0];
                        int accuracy = event.accuracy;

                        Log.i(TAG, Utilities.mSecsToString(actualTimeInMseconds) + ": current thermometer temperature: " + currentTemperatureValue + " with accuracy: " + accuracy);
                        lastReportTime = event.timestamp;
                        // if we have not see any movement on the side of the accelerometer, let's stop


                    }
                }
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
        }
    }

    private boolean standardTemperatureSensorAvailable() { return (mThermometerSensor != null); }
    public void startSensingTemperature() {

        // if the sensor is null,then mSensorManager is null and we get a crash
        if (standardTemperatureSensorAvailable()) {
            Log.d("Standard Thermometer", "starting listener");

            // delay is in microseconds (1millisecond=1000 microseconds)
            // it does not seem to work though
            //stopBarometer();
            // otherwise we stop immediately because

            mSensorManager.registerListener(mTemperatureListener, mThermometerSensor,(int) (mSamplingRateInMSecs * 1000));
            setStarted(true);
        } else {
            Log.i(TAG, "barometer unavailable or already active");
        }
    }


    /**
     * this stops the barometer
     */
    public void stopBarometer() {
        if (standardTemperatureSensorAvailable()) {
            Log.d("Standard Thermometer", "Stopping listener");
            try {
                mSensorManager.unregisterListener(mTemperatureListener);
            } catch (Exception e) {
                // probably already unregistered
            }
        }
        setStarted(false);
    }


    public void setStarted(boolean started) {
        this.started = started;
    }

    public float getCurrentTemperatureValue() {
        return currentTemperatureValue;
    }
}

