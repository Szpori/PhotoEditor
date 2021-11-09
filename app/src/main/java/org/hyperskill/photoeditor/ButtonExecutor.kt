package org.hyperskill.photoeditor

import android.widget.Button


class ButtonExecutor(
    val btnHandler: ButtonHandler,
    saveButton:Button
) {
    val saveButton:Button = saveButton

    init {
        saveButton.setOnClickListener {
            btnHandler.saveImage()
        }
    }
}