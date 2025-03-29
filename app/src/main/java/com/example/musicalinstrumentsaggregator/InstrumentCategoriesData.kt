package com.example.musicalinstrumentsaggregator

object InstrumentCategoriesData {
    fun getIconList(): List<IconItem> {
        return listOf(
            IconItem("Acoustic Guitars", R.drawable.ic_acoustic_guitar),
            IconItem("Electric Guitars", R.drawable.ic_electric_guitar),
            IconItem("Classical Guitars", R.drawable.ic_classical_guitar),
            IconItem("Bass Guitars", R.drawable.ic_bass_guitar),
            IconItem("Left-Hand Guitars", R.drawable.ic_left_hand_guitar),
            IconItem("Trans-Acoustic Guitars", R.drawable.ic_trans_acoustic_guitar),
            IconItem("Pianos", R.drawable.ic_piano),
            IconItem("Stage Pianos", R.drawable.ic_stage_piano),
            IconItem("Keyboards", R.drawable.ic_keyboard),
            IconItem("Synthesizers", R.drawable.ic_synthesizer),
            IconItem("Midi Keyboards", R.drawable.ic_midi_keyboard),
            IconItem("Acoustic Drums", R.drawable.ic_acoustic_drums),
            IconItem("Electronic Drums", R.drawable.ic_electronic_drums),
            IconItem("Violins", R.drawable.ic_violins),
            IconItem("Violas", R.drawable.ic_viola),
            IconItem("Cellos", R.drawable.ic_cello)
        )
    }
}
