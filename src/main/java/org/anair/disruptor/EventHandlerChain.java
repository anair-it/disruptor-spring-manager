package org.anair.disruptor;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;

import com.lmax.disruptor.EventHandler;

/**
 * Design a eventprocessor/consumer dependency graph.
 * <p>Set current event processor(s) and their dependent(next) event processors.
 * This will help define the dependency barriers.
 * 
 * @author Anoop Nair
 *
 * @param <T>
 */
public class EventHandlerChain<T> {

	private EventHandler<T>[] currentEventHandlers;
	private EventHandler<T>[] nextEventHandlers;
	
	public EventHandlerChain(EventHandler<T>[] currentEventHandlers,
			EventHandler<T>[] nextEventHandlers) {
		Validate.notEmpty(currentEventHandlers, "Atleast one Event handler should be present to consume off the ring buffer.");
		this.currentEventHandlers = currentEventHandlers;
		this.nextEventHandlers = nextEventHandlers;
	}
	
	public EventHandlerChain(EventHandler<T>[] currentEventHandlers) {
		this(currentEventHandlers, null);
	}

	public EventHandler<T>[] getCurrentEventHandlers() {
		return currentEventHandlers;
	}

	public EventHandler<T>[] getNextEventHandlers() {
		return nextEventHandlers;
	}

	/**
	 * Print event processor dependency graph.
	 * 
	 */
	public String printDependencyGraph() {
		StringBuilder str = new StringBuilder();
		
		//print current Event handlers
		printEventHandlers(str, getCurrentEventHandlers());
		
		//print dependent Event handlers
		if(! ArrayUtils.isEmpty(getNextEventHandlers())){
			str.append(" -> ");	
			printEventHandlers(str, getNextEventHandlers());
		}
		return str.toString();
	}

	private void printEventHandlers(StringBuilder str, EventHandler<T>[] eventHandlers) {
		str.append("{");
		for(int j=0;j<eventHandlers.length;j++){
			str.append(eventHandlers[j].getClass().getSimpleName());
			str.append(" | ");
		}
		str.delete(str.length()-3, str.length());
		str.append("}");
	}
}
