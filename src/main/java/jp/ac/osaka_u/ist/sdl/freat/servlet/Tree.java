package jp.ac.osaka_u.ist.sdl.freat.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Tree {
	
	private String name;
	
	private List<Tree> contents;
	
	public Tree() {
		this.contents = new ArrayList<Tree>();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(final String name) {
		this.name = name;
	}
	
	public List<Tree> getContents() {
		return Collections.unmodifiableList(contents);
	}
	
	public void addContent(final Tree child) {
		if (child == null) {
			throw new IllegalArgumentException();
		}
		
		this.contents.add(child);
	}
	
	public void addContents(final Collection<Tree> children) {
		if (children == null) {
			throw new IllegalArgumentException();
		}
		
		for (final Tree child : children) {
			addContent(child);
		}
	}

}
