package com.example.calculator

class MyStack<T>(private val maxSize: Int) {
    private val data = arrayOfNulls<Any>(maxSize) as Array<T?>
    private var top: Int = -1

    fun push(`val`: T) {
        data[++top] = `val`
    }

    fun pop(): T? {
        val temp = data[top]
        data[top--] = null
        return temp
    }

    fun peek(): T? {
        return data[top]
    }

    fun isEmpty(): Boolean {
        return top == -1
    }

    fun isFull(): Boolean {
        return top == maxSize - 1
    }

    fun size(): Int {
        return top + 1
    }

}


