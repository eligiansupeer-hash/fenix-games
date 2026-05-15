package com.fenixgames.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fenixgames.domain.model.ContentRating
import com.fenixgames.domain.model.GameMode
import com.fenixgames.domain.model.PenaltyPolicy

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenDiagnostics: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var titleTaps by remember { mutableIntStateOf(0) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Fenix Games",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    titleTaps += 1
                    if (titleTaps >= 7) {
                        titleTaps = 0
                        onOpenDiagnostics()
                    }
                }
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Juego presencial para adolescentes y adultos. Nada de redes, nada externo: solo la ronda.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(20.dp))

            when {
                state.isLoading -> CircularProgressIndicator()
                state.error != null -> Text(
                    text = state.error.orEmpty(),
                    color = MaterialTheme.colorScheme.error
                )
                !state.legalAccepted -> SetupPanel(
                    state = state,
                    onPlayersChange = viewModel::updatePlayers,
                    onMode = viewModel::selectMode,
                    onRating = viewModel::selectRating,
                    onPenalty = viewModel::selectPenalty,
                    onAccept = viewModel::startAcceptedSession
                )
                else -> GamePanel(
                    state = state,
                    onNext = viewModel::nextCard
                )
            }
        }
    }
}

@Composable
private fun SetupPanel(
    state: HomeUiState,
    onPlayersChange: (String) -> Unit,
    onMode: (GameMode) -> Unit,
    onRating: (ContentRating) -> Unit,
    onPenalty: (PenaltyPolicy) -> Unit,
    onAccept: () -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.playersText,
        onValueChange = onPlayersChange,
        label = { Text("Jugadores presentes, separados por coma") }
    )
    Spacer(Modifier.height(16.dp))
    Text("Modo", fontWeight = FontWeight.SemiBold)
    ChipRows {
        GameModeChip("Yo Nunca", GameMode.NEVER_HAVE_I_EVER, state.selectedMode, onMode)
        GameModeChip("Verdad o Reto", GameMode.TRUTH_OR_DARE, state.selectedMode, onMode)
        GameModeChip("Preguntas", GameMode.QUESTIONS, state.selectedMode, onMode)
        GameModeChip("Ruleta", GameMode.ROULETTE, state.selectedMode, onMode)
        GameModeChip("Previa", GameMode.PREVIA, state.selectedMode, onMode)
        GameModeChip("Argento", GameMode.ARGENTO, state.selectedMode, onMode)
    }
    Spacer(Modifier.height(16.dp))
    Text("Nivel", fontWeight = FontWeight.SemiBold)
    ChipRows {
        RatingChip(ContentRating.TEEN, state.selectedRating, onRating)
        RatingChip(ContentRating.ADULT_1, state.selectedRating, onRating)
        RatingChip(ContentRating.ADULT_2, state.selectedRating, onRating)
        RatingChip(ContentRating.ADULT_3, state.selectedRating, onRating)
        RatingChip(ContentRating.ADULT_4, state.selectedRating, onRating)
        RatingChip(ContentRating.ADULT_5, state.selectedRating, onRating)
        RatingChip(ContentRating.ADULT_6, state.selectedRating, onRating)
    }
    Spacer(Modifier.height(16.dp))
    Text("Si alguien no cumple", fontWeight = FontWeight.SemiBold)
    ChipRows {
        PenaltyChip(PenaltyPolicy.LOSE_POINT, state.selectedPenalty, onPenalty)
        PenaltyChip(PenaltyPolicy.LOWER_LEVEL_CHALLENGE, state.selectedPenalty, onPenalty)
        PenaltyChip(PenaltyPolicy.REMOVE_GARMENT, state.selectedPenalty, onPenalty)
    }
    Spacer(Modifier.height(16.dp))
    LegalNotice(state.selectedRating)
    Spacer(Modifier.height(16.dp))
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onAccept
    ) {
        Text("Acepto e iniciar")
    }
}

@Composable
private fun LegalNotice(rating: ContentRating) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Aviso legal", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (rating.adult) {
                    "Este nivel contiene contenido adulto +18. Puede incluir besos, caricias, proxemica, prendas o consignas sexuales segun el nivel. Todas las personas presentes aceptan jugar este nivel. Quien no quiera cumplir una consigna recibe la penalizacion elegida por el grupo."
                } else {
                    "Este nivel es adolescente: picardia social presencial, humor, verguenza y chamuyo sin contenido sexual explicito."
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun GamePanel(
    state: HomeUiState,
    onNext: () -> Unit
) {
    Text(
        text = "${state.selectedMode.name} / ${state.selectedRating.label}",
        style = MaterialTheme.typography.bodyMedium
    )
    Spacer(Modifier.height(12.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = "Ronda ${state.roundIndex} - Turno de ${state.actorName ?: "-"}",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = state.currentCardText ?: "Mazo agotado para este modo y nivel.",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Si no cumple: ${state.selectedPenalty.label}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
    Spacer(Modifier.height(20.dp))
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = state.currentCardText != null,
        onClick = onNext
    ) {
        Text("Siguiente carta")
    }
}

@Composable
private fun ChipRows(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}

@Composable
private fun GameModeChip(
    label: String,
    mode: GameMode,
    selectedMode: GameMode,
    onSelectMode: (GameMode) -> Unit
) {
    FilterChip(
        selected = selectedMode == mode,
        onClick = { onSelectMode(mode) },
        label = { Text(label) }
    )
}

@Composable
private fun RatingChip(
    rating: ContentRating,
    selectedRating: ContentRating,
    onSelectRating: (ContentRating) -> Unit
) {
    FilterChip(
        selected = selectedRating == rating,
        onClick = { onSelectRating(rating) },
        label = { Text(rating.label) }
    )
}

@Composable
private fun PenaltyChip(
    policy: PenaltyPolicy,
    selectedPolicy: PenaltyPolicy,
    onSelectPolicy: (PenaltyPolicy) -> Unit
) {
    FilterChip(
        selected = selectedPolicy == policy,
        onClick = { onSelectPolicy(policy) },
        label = { Text(policy.label) }
    )
}

