package org.anair.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

/**
 * Defines Ring buffer Wait strategies and initializes them. 
 * 
 * @author Anoop Nair
 *
 */
public enum WaitStrategyType {
        /**
         * @see com.lmax.disruptor.BlockingWaitStrategy
         */
        BLOCKING {
            public WaitStrategy instance() {
                return new BlockingWaitStrategy();
            }
        },

        /**
         * @see com.lmax.disruptor.BusySpinWaitStrategy
         */
        BUSY_SPIN {
            public WaitStrategy instance() {
                return new BusySpinWaitStrategy();
            }
        },

        /**
         * @see com.lmax.disruptor.LiteBlockingWaitStrategy
         */
        LITE_BLOCKING {
            public WaitStrategy instance() {
                return new LiteBlockingWaitStrategy();
            }
        },
        
        /**
         * @see com.lmax.disruptor.SleepingWaitStrategy
         */
        SLEEPING_WAIT {
            public WaitStrategy instance() {
                return new SleepingWaitStrategy();
            }
        },
        
        /**
         * @see com.lmax.disruptor.YieldingWaitStrategy
         */
        YIELDING {
            public WaitStrategy instance() {
                return new YieldingWaitStrategy();
            }
        };
        
        abstract WaitStrategy instance();
}
