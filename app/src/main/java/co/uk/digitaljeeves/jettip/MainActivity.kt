package co.uk.digitaljeeves.jettip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.uk.digitaljeeves.jettip.components.InputField
import co.uk.digitaljeeves.jettip.ui.theme.JetTipTheme
import co.uk.digitaljeeves.jettip.util.calculateTotalPerPerson
import co.uk.digitaljeeves.jettip.util.calculateTotalTip
import co.uk.digitaljeeves.jettip.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                Column{
                    MainContent()
                }
            }
        }
    }
}
@Composable
fun MyApp(content: @Composable ()-> Unit){
    JetTipTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background,
            content = content
        )
    }
}
//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 134.0){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .clip(shape = CircleShape.copy(all = CornerSize(12.dp)))
        .padding(15.dp),
        color = Color(0xFFE9D7F7),
        ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(text = "Total per Person",
            style = MaterialTheme.typography.h5)
            Text(text = "£$total", style = MaterialTheme.typography.h4, fontWeight = FontWeight.ExtraBold)
        }
    }
}
@Preview
@Composable
fun MainContent(){
    val range = IntRange(start = 1, endInclusive = 100)
    val splitByState = remember{
        mutableStateOf(1)
    }
    val tipAmountState = remember{
        mutableStateOf(0.0)
    }
    val totalPerPersonState = remember{
        mutableStateOf(0.0)
    }
    Column(modifier = Modifier.padding(all = 12.dp)) {
        BillForm(range = range, splitByState = splitByState, tipAmountState = tipAmountState, totalPerPersonState = totalPerPersonState){}
    }

}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier = Modifier,
             range: IntRange = 1..100,
             splitByState: MutableState<Int>,
             tipAmountState: MutableState<Double>,
             totalPerPersonState: MutableState<Double>,

             onValChange: (String)-> Unit = {}
){

    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value){
        totalBillState.value.trim().isNotEmpty()

    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPositionState.value * 100).toInt()

    TopHeader(totalPerPerson = totalPerPersonState.value)
    Surface(modifier = modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions{
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )
            if (validState){
                Row(modifier = modifier.padding(3.dp),
                horizontalArrangement = Arrangement.Start) {
                    Text(text = "Split",
                    modifier = modifier.align(alignment = Alignment.CenterVertically))
                    Spacer(modifier = modifier.width(120.dp))
                    Row(
                        modifier = modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                if (splitByState.value > range.first){
                                    splitByState.value--
                                }else{
                                    splitByState.value = 1
                                }
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage
                                )
                            }
                        )
                        Text(text = splitByState.value.toString(),
                            modifier = modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                            )
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByState.value <range.last) {
                                    splitByState.value++
                                }

                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage
                                )
                            }
                        )
                    }
                }
                // Tip row
                Row(modifier = modifier.padding(horizontal = 3.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Start) {
                    Text(text = "Tip",
                        modifier = modifier.align(alignment = Alignment.CenterVertically))
                    Spacer(modifier = modifier.width(200.dp))
                    Text(text = "£ ${tipAmountState.value}",
                        modifier = modifier.align(alignment = Alignment.CenterVertically))
                }
                // Tip Percentage
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "$tipPercentage %")
                Spacer(modifier = modifier.height(14.dp))
                //Slider
                Slider(
                    value = sliderPositionState.value,
                    onValueChange = {
                        newVal ->
                        sliderPositionState.value = newVal
                        tipAmountState.value = calculateTotalTip(totalBill = totalBillState.value.toDouble(), tipPercentage = tipPercentage)
                        totalPerPersonState.value = calculateTotalPerPerson(
                            totalBill = totalBillState.value.toDouble(),
                            splitBy = splitByState.value,
                            tipPercentage = tipPercentage
                        )
                    },
                    modifier = modifier.padding(start = 16.dp, end = 16.dp),
                    steps = 5
                )
            }
            }else{
                Box {}
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetTipTheme {
        MyApp {
            Column {
                MainContent()
            }

        }
    }
}