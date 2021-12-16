package com.example.calculator

import de.blox.treeview.TreeNode


class MyTree<T>(root: T? = null) {

    class Node<T>(_data: T) : TreeNode(_data) {
        var left: Node<T>? = null
        var right: Node<T>? = null
    }

    private var root: Node<T>? = null

    init {
        if (root != null)
            addRoot(root)
    }

    fun isEmpty(): Boolean {
        return root == null
    }

    fun addRoot(`val`: T) {
        if (isEmpty()) root = Node(`val`)
    }

    fun getRoot(): Node<T> {
        return root!!
    }

    fun attach(p: Node<T>, t1: MyTree<T>, t2: MyTree<T>?) {
        p.left = t1.root
        p.addChild(t1.root)
        deleteTree(t1)
        if(t2 != null) {
            p.right = t2.root
            p.addChild(t2.root)
            deleteTree(t2)
        }
    }

    fun setElement(p: Node<T>, e: T) {
        p.data = e
    }

    private fun deleteTree(t: MyTree<T>) {
        t.root = null
    }

    fun inorder(node: Node<T>?) {
        if (node == null) return
        inorder(node.left)
        print("${node.data} ")
        inorder(node.right)
    }

}