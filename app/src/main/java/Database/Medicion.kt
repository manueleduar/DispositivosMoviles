/*
    Copyright (C) 2018 - ITESM

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package Database

import NetworkUtility.NetworkConnection
import android.arch.persistence.room.*
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.sql.Date

/*
 * Declares the measurement data that is stored in the database
 */
@Parcelize
@Entity(tableName = "Medicion")
data class Medicion(@ColumnInfo(name = "appSistolica") var appSistolica: String?,
                    @ColumnInfo(name = "appDiastolica") var appDiastolica: String?,
                    @ColumnInfo(name = "manSistolica") var manSistolica: String?,
                    @ColumnInfo(name = "manDiastolica") var manDiastolica: String?,
                    @ColumnInfo(name = "fecha") var fecha: String?,
                    @ColumnInfo(name = "verificado") var verificado: Boolean?,
                    @ColumnInfo(name ="brazo") var brazo: String?,
                    @ColumnInfo(name = "grafica") var grafica: ByteArray?,
                    @ColumnInfo(name = "iniciales") var iniciales: String?): Parcelable {

    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var _id:Int = 0

    companion object {
        fun populateMeds(context: Context) : MutableList<Medicion> = cargaMeds()
        fun populatePatients(context: Context): MutableList<Patient> = cargaPatients()
    }
}

/*
@Parcelize
@Entity(tableName = "Clinics")
data class Clinica(@ColumnInfo(name = "email") var mailC: String?,
                    @ColumnInfo(name = "name") var nameC: String?): Parcelable {

    @ColumnInfo(name = "_idC")
    @PrimaryKey(autoGenerate = true)
    var _idC:Int = 0
}
*/

@Parcelize
@Entity(tableName = "Patient")
data class Patient(@ColumnInfo(name = "email") var mailC: String?,
                   @ColumnInfo(name = "Fname") var FNameP: String?,
                   @ColumnInfo(name = "LName") var LNameP: String?,
                   @ColumnInfo(name = "age") var ageP: Int?,
                   @ColumnInfo(name = "sex") var sexP: String?,
                   @ColumnInfo(name = "clinic") var clinicP: String?): Parcelable {

    @ColumnInfo(name = "_idP")
    @PrimaryKey(autoGenerate = true)
    var _idP:Int = 0
}
/*
    "id": 1,
    "email": "rodrigocampos@example.net",
    "first_name": "Elvia",
    "last_name": "Modesto",
    "age": 13,
    "sex": "F"
*/

fun cargaMeds() : MutableList<Medicion> {
    val url = NetworkConnection.buildUrlPressures()
    // "https://fierce-chamber-37767.herokuapp.com/pressures/"
    val dataJson :String? = NetworkConnection.getResponseFromHttpUrl(url)

    return parseJsonMeds(dataJson!!)
}

/*
        "id": 1,
        "email": "rodrigocampos@example.net",
        "first_name": "Elvia",
        "last_name": "Modesto",
        "age": 13,
        "sex": "F"
 */
fun cargaPatients() : MutableList<Patient>{
    val urlP = NetworkConnection.buildUrlPatients()
    val dataJsonP : String? = NetworkConnection.getResponseFromHttpUrl(urlP)

    return parseJsonPats(dataJsonP!!)
}

fun parseJsonMeds(jsonString: String): MutableList<Medicion>{
    var pressures : MutableList<Medicion> = mutableListOf()
    var press : Medicion
    //Primero es array
    try {
        val dataJsonList : JSONArray = JSONArray(jsonString)
        for(i in 0 until 10){
            val jsonMed : JSONObject = dataJsonList.getJSONObject(i)
            val dateMed = jsonMed.getString("date")
            val verMed = jsonMed.getBoolean("verified")
            val sysMed = jsonMed.getInt("systolic").toString()
            val disMed = jsonMed.getInt("diastolic").toString()
            val m_sysMed = jsonMed.getInt("manual_systolic").toString()
            val m_disMed = jsonMed.getInt("manual_diastolic").toString()
            val armMedMed = jsonMed.getString("arm")
            //patient object
            val patientMed = jsonMed.getJSONObject("patient")
            val patientNameMed = patientMed.getString("first_name")

            press = Medicion(sysMed,disMed,m_sysMed,m_disMed,dateMed,verMed,armMedMed,null,patientNameMed)
            pressures.add(press)
        }
    }catch (e: JSONException) {
        e.printStackTrace()
        throw IOException("JSONException")
    }
    return pressures
}

fun parseJsonPats(jsonString: String): MutableList<Patient>{
    var patients : MutableList<Patient> = mutableListOf()
    var pat : Patient
    //Primero es array
    try {
        val dataJsonList : JSONArray = JSONArray(jsonString)
        for(i in 0 until 20){
            val jsonPat : JSONObject = dataJsonList.getJSONObject(i)
            val patMail = jsonPat.getString("email")
            val patFname = jsonPat.getString("first_name")
            val patLname = jsonPat.getString("last_name")
            val patAge = jsonPat.getInt("age")
            val patSex = jsonPat.getString("sex")

            pat = Patient(patMail,patFname,patLname,patAge,patSex,null)
            patients.add(pat)
        }
    }catch (e: JSONException) {
        e.printStackTrace()
        throw IOException("JSONException")
    }
    return patients
}