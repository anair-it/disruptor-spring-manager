package org.anair.disruptor;

import static org.junit.Assert.*;

import org.junit.Test;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

public class WaitStrategyTypeTest {
	
	@Test
	public void test_All_WaitStrategies() {
		assertTrue(WaitStrategyType.BLOCKING.instance() instanceof BlockingWaitStrategy);
		assertTrue(WaitStrategyType.BUSY_SPIN.instance() instanceof BusySpinWaitStrategy);
		assertTrue(WaitStrategyType.LITE_BLOCKING.instance() instanceof LiteBlockingWaitStrategy);
		assertTrue(WaitStrategyType.SLEEPING_WAIT.instance() instanceof SleepingWaitStrategy);
		assertTrue(WaitStrategyType.YIELDING.instance() instanceof YieldingWaitStrategy);
	}
	
}
