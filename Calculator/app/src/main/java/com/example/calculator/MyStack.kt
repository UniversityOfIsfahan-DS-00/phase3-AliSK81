package com.example.calculator

class MyStack<T>(private val maxSize: Int) {
    private val data: Array<T?> = arrayOfNulls<Any>(maxSize) as Array<T?>
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

    val isEmpty: Boolean
        get() = top == -1

    val isFull: Boolean
        get() = top == maxSize - 1

    fun size(): Int {
        return top + 1
    }

}


