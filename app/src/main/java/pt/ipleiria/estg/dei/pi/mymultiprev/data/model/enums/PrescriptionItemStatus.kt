package pt.ipleiria.estg.dei.pi.mymultiprev.data.model.enums

enum class PrescriptionItemStatus {
    NotAcquired {
        override fun value(): Int = 0
    },
    InProgress {
        override fun value(): Int = 1
    },
    Completed {
        override fun value(): Int = 2
    };

    abstract fun value(): Int
}