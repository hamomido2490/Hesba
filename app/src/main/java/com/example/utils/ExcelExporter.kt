package com.example.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.domain.CalculatorResults
import com.example.domain.CalculatorState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExcelExporter {

    suspend fun exportToExcel(context: Context, state: CalculatorState, results: CalculatorResults): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                val fileName = "HISBA_Report_$dateStr.xls"

                val htmlContent = buildHtmlContent(state, results, dateStr)

                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.ms-excel")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    val outputStream: OutputStream? = resolver.openOutputStream(uri)
                    outputStream?.use {
                        it.write(htmlContent.toByteArray(Charsets.UTF_8))
                    }
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun buildHtmlContent(state: CalculatorState, results: CalculatorResults, dateStr: String): String {
        val sb = java.lang.StringBuilder()
        sb.append("<html dir='rtl'>")
        sb.append("<head><meta charset='UTF-8'><style>table { border-collapse: collapse; width: 100%; } th, td { border: 1px solid black; padding: 8px; text-align: right; }</style></head>")
        sb.append("<body>")
        sb.append("<h2>\uD83D\uDCCA تقرير حِسْبَة - HISBA Cost Report</h2>")
        sb.append("<p>تاريخ التصدير: $dateStr</p>")
        sb.append("<p>المنتج: ${state.productName}</p>")
        sb.append("<p>العملة: ${state.currency}</p>")
        
        sb.append("<table>")
        sb.append("<tr style='background-color:#00E5FF; color:black;'>")
        sb.append("<th>المكون</th><th>الكمية</th><th>الوحدة</th><th>السعر</th><th>الهالك %</th><th>التكلفة الأساسية</th><th>بعد الهالك</th>")
        sb.append("</tr>")

        for (comp in state.components) {
            val q = comp.qty.toFloatOrNull() ?: 0f
            val p = comp.price.toFloatOrNull() ?: 0f
            val w = comp.wastePercent.toFloatOrNull() ?: 0f
            val (base, withWaste) = com.example.domain.UnitSystem.calculateComponentCost(q, comp.unit, p, w)
            
            sb.append("<tr>")
            sb.append("<td>${comp.name}</td>")
            sb.append("<td>$q</td>")
            sb.append("<td>${comp.unit}</td>")
            sb.append("<td>$p</td>")
            sb.append("<td>$w%</td>")
            sb.append("<td>$base</td>")
            sb.append("<td>$withWaste</td>")
            sb.append("</tr>")
        }

        sb.append("<tr style='background-color:#B537F2; color:white;'><td colspan='7'><b>الإجماليات</b></td></tr>")
        sb.append("<tr><td colspan='6'>إجمالي التكلفة الأساسية</td><td>${results.baseCostTotal}</td></tr>")
        sb.append("<tr><td colspan='6'>إجمالي الهالك (مكونات + عام ${state.generalWastePercent}%)</td><td>${results.totalWaste}</td></tr>")
        sb.append("<tr><td colspan='6'>التكلفة النهائية</td><td>${results.totalCost}</td></tr>")
        sb.append("<tr><td colspan='6'>الربح</td><td>${results.profit}</td></tr>")
        sb.append("<tr style='background-color:#00FFA3; color:black;'><td colspan='6'><b>سعر البيع النهائي</b></td><td><b>${results.sellPrice}</b></td></tr>")

        sb.append("</table>")
        sb.append("</body></html>")
        return sb.toString()
    }
}
