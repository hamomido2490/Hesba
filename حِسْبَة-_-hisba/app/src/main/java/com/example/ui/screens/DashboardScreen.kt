package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProductEntity
import com.example.domain.CalculatorResults
import com.example.domain.CalculatorState
import com.example.domain.ComponentItem
import com.example.domain.ProfitMode
import com.example.domain.UnitSystem
import com.example.ui.HisbaViewModel
import com.example.ui.components.NeonPieChart
import com.example.ui.theme.*
import com.example.utils.ExcelExporter
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(viewModel: HisbaViewModel) {
    val state by viewModel.state.collectAsState()
    val results by viewModel.results.collectAsState()
    val savedProducts by viewModel.savedProducts.collectAsState()
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HeaderSection(
                    onClear = { viewModel.clearAll() },
                    onExportExcel = {
                        coroutineScope.launch {
                            val success = ExcelExporter.exportToExcel(context, state, results)
                            if (success) {
                                Toast.makeText(context, "تم تصدير Excel بنجاح", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "فشل تصدير Excel", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }

            item { ProductInfoSection(state, viewModel) }
            
            item { ComponentsTableSection(state, viewModel) }
            
            item { SettingsSection(state, viewModel) }
            
            item { ResultsSection(results, state.currency) }
            
            item { ChartsSection(state) }
            
            item {
                Button(
                    onClick = { viewModel.saveProduct() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(14.dp), ambientColor = NeonCyan, spotColor = NeonCyan),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("حفظ المنتج", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
            
            if (savedProducts.isNotEmpty()) {
                item {
                    Text(
                        "المنتجات المحفوظة (${savedProducts.size})",
                        color = NeonPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                    )
                }
                items(savedProducts) { product ->
                    SavedProductCard(product, onDelete = { viewModel.deleteSavedProduct(it) })
                }
            }
        }
    }
}

@Composable
fun HeaderSection(onClear: () -> Unit, onExportExcel: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("حِسْبَة | HISBA", fontSize = 28.sp, fontWeight = FontWeight.Black, color = NeonCyan)
            Text("حاسبة التكلفة الذكية", color = Color.LightGray, fontSize = 14.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = onExportExcel,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen)
            ) { Text("Excel") }
            OutlinedButton(
                onClick = onClear,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonPink),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPink)
            ) { Text("مسح") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductInfoSection(state: CalculatorState, viewModel: HisbaViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = state.productName,
                onValueChange = { viewModel.updateProductName(it) },
                label = { Text("اسم المنتج") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    focusedLabelColor = NeonCyan
                )
            )
            
            var expanded by remember { mutableStateOf(false) }
            val currencies = listOf("ج.م", "ر.س", "د.إ", "د.ك", "$", "€")
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = state.currency,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("العملة") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    currencies.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                viewModel.updateCurrency(selectionOption)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComponentsTableSection(state: CalculatorState, viewModel: HisbaViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("مكونات المنتج", color = NeonPurple, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            state.components.forEachIndexed { index, component ->
                ComponentRow(
                    index = index + 1,
                    component = component,
                    onUpdate = { viewModel.updateComponent(component.id, it) },
                    onDelete = { viewModel.removeComponent(component.id) }
                )
                HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 8.dp))
            }
            
            TextButton(
                onClick = { viewModel.addComponent() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(contentColor = NeonCyan)
            ) {
                Icon(Icons.Default.Add, contentDescription = "إضافة")
                Spacer(Modifier.width(4.dp))
                Text("إضافة مكوّن")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentRow(index: Int, component: ComponentItem, onUpdate: ((ComponentItem) -> ComponentItem) -> Unit, onDelete: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text("#$index", color = NeonCyan)
            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = NeonPink, modifier = Modifier.clickable { onDelete() })
        }
        
        OutlinedTextField(
            value = component.name,
            onValueChange = { n -> onUpdate { it.copy(name = n) } },
            label = { Text("اسم المكون") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = component.qty,
                onValueChange = { q -> onUpdate { it.copy(qty = q) } },
                label = { Text("الكمية") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            
            var expanded by remember { mutableStateOf(false) }
            val units = UnitSystem.units.keys.toList()
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = component.unit,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("الوحدة") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    units.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                onUpdate { it.copy(unit = selectionOption) }
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = component.price,
                onValueChange = { p -> onUpdate { it.copy(price = p) } },
                label = { Text("سعر الوحدة الكبرى") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = component.wastePercent,
                onValueChange = { w -> onUpdate { it.copy(wastePercent = w) } },
                label = { Text("هالك %") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SettingsSection(state: CalculatorState, viewModel: HisbaViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = state.generalWastePercent,
                onValueChange = { viewModel.updateGeneralWaste(it) },
                label = { Text("هالك عام إضافي %") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            
            Text("إعدادات الربح", color = NeonCyan, modifier = Modifier.padding(top = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfitMode.values().forEach { mode ->
                    val selected = state.profitMode == mode
                    Button(
                        onClick = { viewModel.updateProfitMode(mode) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) NeonPurple else Color.DarkGray,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(mode.label)
                    }
                }
            }
            
            OutlinedTextField(
                value = state.profitInput,
                onValueChange = { viewModel.updateProfitInput(it) },
                label = { Text("قيمة الربح") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ResultsSection(results: CalculatorResults, currency: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ResultCard("التكلفة الأساسية", results.baseCostTotal, currency, Color.White)
        ResultCard("قيمة الهالك", results.totalWaste, currency, NeonOrange)
        ResultCard("التكلفة النهائية", results.totalCost, currency, NeonPink)
        ResultCard("الربح", results.profit, currency, NeonCyan)
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(14.dp), ambientColor = NeonGreen, spotColor = NeonGreen),
            colors = CardDefaults.cardColors(containerColor = NeonGreen),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("سعر البيع النهائي", color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(String.format("%.2f %s", results.sellPrice, currency), color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun ResultCard(title: String, value: Float, currency: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = Color.LightGray)
            Text(String.format("%.2f %s", value, currency), color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ChartsSection(state: CalculatorState) {
    if (state.components.isEmpty()) return
    
    val values = state.components.map { c -> 
        val q = c.qty.toFloatOrNull() ?: 0f
        val p = c.price.toFloatOrNull() ?: 0f
        val w = c.wastePercent.toFloatOrNull() ?: 0f
        UnitSystem.calculateComponentCost(q, c.unit, p, w).second
    }
    
    val colors = listOf(NeonCyan, NeonPurple, NeonPink, NeonGreen, NeonOrange, Color.Yellow, Color.Red)
    
    Card(
        modifier = Modifier.fillMaxWidth().height(250.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("توزيع تكلفة المكونات", color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.size(150.dp)) {
                NeonPieChart(values = values, colors = colors)
            }
        }
    }
}

@Composable
fun SavedProductCard(product: ProductEntity, onDelete: (Long) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(product.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(product.date, color = Color.Gray, fontSize = 12.sp)
                Text("التكلفة: ${product.totalCost} ${product.currency}", color = NeonPink, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${product.sellPrice} ${product.currency}", color = NeonGreen, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Icon(Icons.Default.Delete, contentDescription = "حذف", tint = NeonPink, modifier = Modifier.clickable { onDelete(product.id) })
            }
        }
    }
}
