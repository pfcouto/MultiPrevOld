package pt.ipleiria.estg.dei.pi.mymultiprev.data.model.enums

enum class LayoutPreferences {
    LAYOUT_FULL {
        override fun value(): Boolean = true
    },
    LAYOUT_SHORT {
        override fun value(): Boolean = false
    };

    abstract fun value(): Boolean

    companion object {
        fun getLayoutPreferenceFromValue(value: Boolean) = if (value) LAYOUT_FULL else LAYOUT_SHORT
    }
}