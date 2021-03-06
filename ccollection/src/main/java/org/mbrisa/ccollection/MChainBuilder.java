package org.mbrisa.ccollection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MChainBuilder<E> implements CCBuilder<E> {
	
	private final LinkedList<Chain<E>> chainList = new LinkedList<>();
	private final LinkedCondition<E> chainCondition;
	private final NoCompleteHandler noCompletion;
	private final LinkedList<E> scrap = new LinkedList<>();
	
	public MChainBuilder(LinkedCondition<E> chainCondition,NoCompleteHandler handler) {
		this.chainCondition = chainCondition;
		this.noCompletion = handler;
	}
	
	public MChainBuilder(LinkedCondition<E> chainCondition) {
		this(chainCondition, new GruffNoCompleteHandler());
	}
	
	public void add(E node) {
		for(Chain<E> chain : this.chainList){
			if(chain.add(node)){
				this.reLink();
				return;
			}
		}
		Chain<E> nChain = new Chain<E>(chainCondition);
		if(nChain.add(node)){
			this.chainList.add(nChain);
			if(!this.scrap.isEmpty()){ // all chain can not link the new node.so if scrap is empty ,no need relink on old chain
				reLink();
			}
			return;
		}
		this.scrap.add(node);
	}
	
	private void reLink() {
		if(this.scrap.size() > 0){
			for(E e : scrap){
				for(Chain<E> chain : this.chainList){
					if(chain.add(e)){
						this.scrap.remove(e);
						reLink();
						return;
					}
				}
			}
		}
		
		if(this.chainList.size() > 1){
			for(int i = 0;i < this.chainList.size(); i++){
				Chain<E> chainI = this.chainList.get(i);
				
				for(int j = 0; j < this.chainList.size(); j++){
					if(i == j){
						continue;
					}
					Chain<E> chainJ = this.chainList.get(j);
					try {
						if(chainI.add(chainJ)){
							this.chainList.remove(j);
							reLink();
							return ;
						}
					} catch (NoCompatibilityException e) {
						assert(false);// must compatible with same chainCondition
					}
				}
			}
		}
	}
	
	
	public List<Chain<E>> retrieve(){
		validateScrap();
		return new ArrayList<>(this.chainList);
	}
	
	public Chain<E> retrieveFirst(){
		validateScrap();
		if(this.chainList.size() == 0){
			return null;
		}
		return this.chainList.get(0);
	}
	
	private void validateScrap(){
		if(!this.isComplete()){
			noCompletion.handle();
		}
	}
	
	public boolean isComplete(){
		return this.scrap.isEmpty();
	}
	
	public boolean remove(Chain<E> chain){
		return this.chainList.remove(chain);
	}
	
	public int chainCount() {
		return this.chainList.size();
	}
	
	public List<E> retrieveScrap(){
		return new ArrayList<>(this.scrap);
	}
	
	public void clear(){
		this.chainList.clear();
		this.scrap.clear();
	}
	
	@Override
	public int size() {
		int size = 0;
		for(Chain<E> c : this.chainList){
			size += c.size();
		}
		return size;
	}
	
}
