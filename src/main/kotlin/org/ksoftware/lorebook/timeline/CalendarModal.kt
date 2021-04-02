package org.ksoftware.lorebook.timeline

import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import tornadofx.*
import java.util.*

class CalendarModal {
    val nameProperty = SimpleStringProperty(UUID.randomUUID().toString().take(4))
    val eras = SimpleListProperty(FXCollections.observableArrayList<EraModal>())
    val months = SimpleListProperty(FXCollections.observableArrayList<MonthModal>())
    val days = SimpleListProperty( FXCollections.observableArrayList<DayModal>())
}

class CalendarViewModal : ItemViewModel<CalendarModal>() {
    val name = bind(CalendarModal::nameProperty, autocommit = true)
    val eras = bind(CalendarModal::eras)
    val months = bind(CalendarModal::months)
    val days = bind(CalendarModal::days)
}

class EraModal {
    val nameProperty = SimpleStringProperty(UUID.randomUUID().toString().take(4))
    val startYearProperty = SimpleLongProperty(0L)
    var startYear by startYearProperty
    val durationInYearsProperty = SimpleLongProperty(1000L)
    var durationInYears by durationInYearsProperty
    val hasLeapYearsProperty = SimpleBooleanProperty(false)
    var hasLeapYears by hasLeapYearsProperty
}

class EraViewModal(model: EraModal = EraModal()) : ItemViewModel<EraModal>() {
    init {
        item = model
    }
    val name = bind(EraModal::nameProperty, autocommit = true)
    val startYear = bind(EraModal::startYearProperty, autocommit = true)
    val durationInYears = bind(EraModal::durationInYearsProperty, autocommit = true)
    val hasLeapYears = bind(EraModal::hasLeapYearsProperty, autocommit = true)
}

class MonthModal {
    val nameProperty = SimpleStringProperty("myMonthName")
    val shortNameProperty = SimpleStringProperty("myMonthShortName")
    val durationInDaysProperty = SimpleIntegerProperty(30)
    val durationInDaysDuringLeapYearProperty = SimpleIntegerProperty(30)
}

class MonthViewModal(model: MonthModal = MonthModal()) : ItemViewModel<MonthModal>() {
    init {
        item = model
    }
    val name = bind(MonthModal::nameProperty, autocommit = true)
    val shortName = bind(MonthModal::shortNameProperty, autocommit = true)
    val durationInDays = bind(MonthModal::durationInDaysProperty, autocommit = true)
    val durationInDaysDuringLeapYear = bind(MonthModal::durationInDaysDuringLeapYearProperty, autocommit = true)
}

class DayModal {
    val nameProperty = SimpleStringProperty("Day Name")
    val shortNameProperty = SimpleStringProperty("myDayShortName")
    val durationInHoursProperty = SimpleIntegerProperty(24)
}

class DayViewModal(model: DayModal = DayModal()) : ItemViewModel<DayModal>() {
    init {
        item = model
    }
    val name = bind(DayModal::nameProperty, autocommit = true)
    val shortName = bind(DayModal::shortNameProperty, autocommit = true)
    val durationInHours = bind(DayModal::durationInHoursProperty, autocommit = true)
}