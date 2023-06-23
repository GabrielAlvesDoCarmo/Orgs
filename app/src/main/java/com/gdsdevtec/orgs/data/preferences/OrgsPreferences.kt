import android.content.Context

class OrgsPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("orgs_preferences", Context.MODE_PRIVATE)

    fun saveColorStatusBar(value: Int) {
        sharedPreferences.edit().putInt(STATUS_BAR, value).apply()
    }

    fun getColorStatusBar(): Int {
        return sharedPreferences.getInt(STATUS_BAR,0)
    }

    fun saveColorAppBar(value: Int) {
        sharedPreferences.edit().putInt(APP_BAR, value).apply()
    }

    fun getColorAppBar(): Int {
        return sharedPreferences.getInt(APP_BAR,0)
    }

    fun saveColorBtn(value: Int) {
        sharedPreferences.edit().putInt(BTN_APP_COLOR, value).apply()
    }

    fun getColorBtn(): Int {
        return sharedPreferences.getInt(BTN_APP_COLOR,0)
    }

    fun saveColorLetters(value: Int) {
        sharedPreferences.edit().putInt(LETTERS_COLORS, value).apply()
    }

    fun getColorLetters(): Int {
        return sharedPreferences.getInt(LETTERS_COLORS,0)
    }

    fun saveColorBackground(value: Int) {
        sharedPreferences.edit().putInt(BACKGROUND_COLOR, value).apply()
    }

    fun getColorBackground(): Int {
        return sharedPreferences.getInt(BACKGROUND_COLOR,0)
    }
    companion object {

        private const val STATUS_BAR = "STATUS_BAR"
        private const val APP_BAR = "APP_BAR"
        private const val BTN_APP_COLOR = "BTN_APP_COLOR"
        private const val LETTERS_COLORS = "LETTERS_COLORS"
        private const val BACKGROUND_COLOR = "BACKGROUND_COLOR"
        @Volatile
        private var instance: OrgsPreferences? = null

        fun getInstance(context: Context): OrgsPreferences {
            return instance ?: synchronized(this) {
                instance ?: OrgsPreferences(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}
