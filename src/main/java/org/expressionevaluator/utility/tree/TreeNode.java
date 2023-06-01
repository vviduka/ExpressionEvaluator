package org.expressionevaluator.utility.tree;

import lombok.Getter;
import lombok.Setter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class TreeNode<T> implements Iterable<TreeNode<T>> {

    public T data;
    public TreeNode<T> parent;
    public List<TreeNode<T>> children;

    public boolean isRoot() {
        return parent == null;
    }

    private List<TreeNode<T>> elementsIndex;

    public TreeNode(T data) {
        this.data = data;
        this.children = new LinkedList<>();
        this.elementsIndex = new LinkedList<>();
        this.elementsIndex.add(this);
    }

    public void addChild(TreeNode<T> childNode) {
        childNode.parent = this;
        this.children.add(childNode);
    }

    @Override
    public String toString() {
        return data != null ? data.toString() : "[data null]";
    }

    @Override
    public Iterator<TreeNode<T>> iterator() {
        return new TreeNodeIterator<T>(this);
    }

}