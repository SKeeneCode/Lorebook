package org.ksoftware.lorebook.styles

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.paint.Color

abstract class NodeStyle
abstract class NodeProperties

/**
 *  --------- STYLES ------------
 */
interface BackgroundColor { val backgroundColor: SimpleObjectProperty<Color> }
interface BorderColor { val borderColor: SimpleObjectProperty<Color> }
interface BorderWidth { val borderWidth: SimpleIntegerProperty }
interface Padding { val padding: SimpleDoubleProperty }

/**
 *  --------- PROPERTIES ------------
 */
interface Width { val width: SimpleDoubleProperty }
interface Height { val double: SimpleDoubleProperty }

class TextNodeStyle(
    override val backgroundColor: SimpleObjectProperty<Color>,
    override val borderColor: SimpleObjectProperty<Color>,
    override val borderWidth: SimpleIntegerProperty
) : NodeStyle(), BackgroundColor, BorderColor, BorderWidth

class TextNodeProperties(
    override val width: SimpleDoubleProperty,
    override val double: SimpleDoubleProperty
) : NodeProperties(), Width, Height


