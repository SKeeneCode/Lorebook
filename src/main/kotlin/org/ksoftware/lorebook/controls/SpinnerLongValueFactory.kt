package org.ksoftware.lorebook.controls

import javafx.beans.property.LongProperty
import javafx.beans.property.SimpleLongProperty
import javafx.scene.control.SpinnerValueFactory
import javafx.util.converter.LongStringConverter

class SpinnerLongValueFactory(val minValue: Long = 0, val maxValue: Long = Long.MAX_VALUE, val initialValue: Long = 1) : SpinnerValueFactory<Long>() {

    init {
        converter = LongStringConverter()
        valueProperty().addListener { _, _, newValue ->
            // when the value is set, we need to react to ensure it is a
            // valid value (and if not, blow up appropriately)
            if (newValue == null || newValue < minValue) {
                value = minValue
            } else if (newValue > maxValue) {
                value = maxValue
            }
        }

        value = if (initialValue in minValue..maxValue) initialValue else minValue
    }

    val minProperty = object : SimpleLongProperty(this, "min", minValue) {
        override fun invalidated() {
            val currentValue = this@SpinnerLongValueFactory.value ?: return
            val newMin = get()
            if (newMin > getMax()) {
                setMin(getMax())
                return
            }
            if (currentValue < newMin) {
                this@SpinnerLongValueFactory.value = newMin
            }
        }
    }

    fun setMin(value: Long) {
        minProperty.set(value)
    }

    fun getMin(): Long {
        return minProperty.get()
    }

    val maxProperty = object : SimpleLongProperty(this, "max", maxValue) {
        override fun invalidated() {
            val currentValue = this@SpinnerLongValueFactory.value ?: return
            val newMax = get()
            if (newMax < getMin()) {
                setMax(getMin())
                return
            }
            if (currentValue > newMax) {
                this@SpinnerLongValueFactory.value = newMax
            }
        }
    }

    fun setMax(value: Long) {
        maxProperty.set(value)
    }

    fun getMax(): Long {
        return maxProperty.get()
    }

    private val amountToStepBy = SimpleLongProperty(this, "amountToStepBy", 1)
    fun setAmountToStepBy(value: Long) {
        amountToStepBy.set(value)
    }

    fun getAmountToStepBy(): Long {
        return amountToStepBy.get()
    }

    /**
     * Sets the amount to increment or decrement by, per step.
     * @return the amount to increment or decrement by, per step
     */
    fun amountToStepByProperty(): LongProperty {
        return amountToStepBy
    }


    /***********************************************************************
     * *
     * Overridden methods                                                  *
     * *
     */
    /** {@inheritDoc}  */
    override fun decrement(steps: Int) {
        val min = getMin()
        val newIndex = (value - steps * getAmountToStepBy())
        value = if (newIndex >= min) {
            newIndex
        } else {
            min
        }
    }

    /** {@inheritDoc}  */
    override fun increment(steps: Int) {
        val min = getMin()
        val max = getMax()
        val currentValue = value
        val newIndex = currentValue + steps * getAmountToStepBy()
        value = if (newIndex <= max) newIndex else if (isWrapAround) (newIndex - min) % (max - min + 1) + min else max

    }
}