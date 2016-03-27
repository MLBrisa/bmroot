package org.mbrisa.ccollection;

@SuppressWarnings("rawtypes")
class NoLimitLinkedCondition implements LinkedCondition {
	private static final NoLimitLinkedCondition INSTANCE = new NoLimitLinkedCondition();

	private NoLimitLinkedCondition() {
	}

	
	public static NoLimitLinkedCondition getInstance(){
		return INSTANCE;
	}
	
	@Override
	public boolean appendable(Object target, Object addition) {
		return true;
	}
}