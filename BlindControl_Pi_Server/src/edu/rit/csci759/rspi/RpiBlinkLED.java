package edu.rit.csci759.rspi;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Examples
 * FILENAME      :  ControlGpioExample.java  
 * 
 * This file is part of the Pi4J project. More information about 
 * this project can be found here:  http://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2014 Pi4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class RpiBlinkLED {

	// Create a gpio controller
	private final GpioController gpio = GpioFactory.getInstance();
	
	/**
	 * @param color - int, representing blind state
	 * 					   0 - green
	 * 					   1 - yellow
	 * 					   2 - red
	 * @throws InterruptedException
	 */
	public void lightLED(int color) { 

		GpioPinDigitalOutput GREEN_PIN = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "MyLED", PinState.LOW);
		GpioPinDigitalOutput YELLOW_PIN = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "MyLED", PinState.LOW);
		GpioPinDigitalOutput RED_PIN = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, "MyLED", PinState.LOW);
				
		if (color == 0) {			// green
			RED_PIN.low();
			YELLOW_PIN.low();
			GREEN_PIN.high();
		} else if (color == 1) {	// yellow
			RED_PIN.low();
			GREEN_PIN.low();
			YELLOW_PIN.high();
		} else if (color == 2) {	// red
			YELLOW_PIN.low();
			GREEN_PIN.low();
			RED_PIN.high();
		} else {						// invalid state code
			System.out.println("Invalid color code passed: " + color);
		}
		
		gpio.unprovisionPin(GREEN_PIN, YELLOW_PIN, RED_PIN);
		
	}
	
	public void shutdown() {
		
		GpioPinDigitalOutput GREEN_PIN = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "MyLED", PinState.LOW);
		GpioPinDigitalOutput YELLOW_PIN = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "MyLED", PinState.LOW);
		GpioPinDigitalOutput RED_PIN = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_29, "MyLED", PinState.LOW);
		
		GREEN_PIN.low();
		RED_PIN.low();
		YELLOW_PIN.low();
		// stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
		gpio.unprovisionPin(GREEN_PIN, YELLOW_PIN, RED_PIN);
        	gpio.shutdown();
	}
}
