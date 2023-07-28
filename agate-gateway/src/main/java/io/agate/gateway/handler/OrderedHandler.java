package io.agate.gateway.handler;

public abstract class OrderedHandler implements RouteHandler, Comparable<OrderedHandler> {

	private int order;

	public OrderedHandler(int order) {
		this.order = order;
	}

	/**
	 * Get the order value of this object.
	 * <p>
	 * Higher values are interpreted as lower priority. As a consequence, the object
	 * with the lowest value has the highest priority (somewhat analogous to Servlet
	 * {@code load-on-startup} values).
	 * <p>
	 * Same order values will result in arbitrary sort positions for the affected
	 * objects.
	 * 
	 * @return the order value
	 */
	public int getOrder() {
		return order;
	}

	@Override
	public int compareTo(OrderedHandler other) {
		if (other == null) {
			return -1;
		}
		return this.order - other.order;
	}
}
