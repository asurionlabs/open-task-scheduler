/**
 Copyright (C) 2018-2019  Asurion, LLC

 Open Task Scheduler is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Open Task Scheduler is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Open Task Scheduler.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.asurion.ava.scheduler.core;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;

/**
 * Generates sequential id.
 * 
 * @author scott.cheng
 *
 */
public class TaskIdGenerator {
	// a timestamp, a worker number/node id, and a sequence number

	/**
	 * Epoch timestamp in milliseconds precision - 42 bits. The maximum timestamp that can be represented using 42 bits is 242 - 1, or 4398046511103, which comes out to be Wednesday, May 15, 2109 7:35:11.103 AM. That gives us 139 years with respect to a custom epoch.
	Node ID - 10 bits. This gives us 1024 nodes/machines.
	Local counter per machine - 12 bits. The counter’s max value would be 4095.
	 */

	private static final int TOTAL_BITS = 64;
	private static final int EPOCH_BITS = 42;
	private static final int NODE_ID_BITS = 10;
	private static final int SEQUENCE_BITS = 12;

	private static final int maxNodeId = (int)(Math.pow(2, NODE_ID_BITS) - 1);
	private static final int maxSequence = (int)(Math.pow(2, SEQUENCE_BITS) - 1);

	// Custom Epoch (January 1, 2015 Midnight UTC = 2015-01-01T00:00:00Z)
	private static final long CUSTOM_EPOCH = 1420070400000L;

	private final int nodeId;

	private long lastTimestamp = -1L;
	private long sequence = 0L;

	private static final TaskIdGenerator instance = new TaskIdGenerator();
	

	// Let SequenceGenerator generate a nodeId
	private TaskIdGenerator() {
		this.nodeId = createNodeId();
	}
	
	public String getNextId() {
        return String.valueOf(nextId());
    }
	
	private long nextId() {
        long currentTimestamp = timestamp();

        synchronized (this) {
            if(currentTimestamp < lastTimestamp) {
                throw new IllegalStateException("Invalid System Clock!");
            }

            if (currentTimestamp == lastTimestamp) {
                sequence = (sequence + 1) & maxSequence;
                if(sequence == 0) {
                    // Sequence Exhausted, wait till next millisecond.
                    currentTimestamp = waitNextMillis(currentTimestamp);
                }
            } else {
                // reset sequence to start with zero for the next millisecond
                sequence = 0;
            }

            lastTimestamp = currentTimestamp;
        }

        long id = currentTimestamp << (TOTAL_BITS - EPOCH_BITS);
        id |= (nodeId << (TOTAL_BITS - EPOCH_BITS - NODE_ID_BITS));
        id |= sequence;
        return id;
    }


    // Get current timestamp in milliseconds, adjust for the custom epoch.
    private static long timestamp() {
        return Instant.now().toEpochMilli() - CUSTOM_EPOCH;
    }

    // Block and wait till next millisecond
    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }

    private int createNodeId() {
        int nodeId;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for(int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X", mac[i]));
                    }
                }
            }
            nodeId = sb.toString().hashCode();
        } catch (Exception ex) {
            nodeId = (new SecureRandom().nextInt());
        }
        nodeId = nodeId & maxNodeId;
        return nodeId;
    }
    
    public static TaskIdGenerator getInstance() {
		return instance;
	}
    
    public static void main(String[] args) {
		for(int i=0; i<1000000; ++i) {
			System.out.println(getInstance().nextId());
		}
	}


}

