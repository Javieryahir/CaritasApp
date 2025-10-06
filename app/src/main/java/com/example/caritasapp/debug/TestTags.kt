package com.example.caritasapp.debug

object TestTags {

    // --- Login ---
    const val LoginGoogleButton = "login_google_button"

    // --- ReservationPage (search) ---
    const val ResFiltersButton = "res_filters_button"
    const val ResShelterPickerButton = "res_shelter_picker_button"
    const val ResFiltersSheetRoot = "filters_sheet_root"
    const val ResFiltersCloseBtn = "filters_close_btn"
    const val ResPickerSheetRoot = "picker_sheet_root"
    const val ResConfirmShelterButton = "res_confirm_shelter_button" // Botón para ir a HealthForms

    // Para items dinámicos
    fun filterService(label: String) = "filter_service_${slug(label)}"
    fun pickerLocation(name: String) = "picker_loc_${slug(name)}"

    // --- HealthForms ---
    const val HFReserveButton = "hf_btn_reservation"
    fun hfTextField(label: String) = "hf_text_${slug(label)}"
    fun hfToggle(label: String) = "hf_toggle_${slug(label)}"

    // --- WaitingPage ---
    const val WaitingPageRoot = "waiting_page_root"
    const val WaitingPageConfirmButton = "waiting_page_confirm_button"

    // Utilidad interna: normaliza strings para usarlos como tags
    private fun slug(raw: String): String = raw
        .lowercase()
        .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
        .replace("ñ", "n")
        .replace("\\s+".toRegex(), "_")
}
