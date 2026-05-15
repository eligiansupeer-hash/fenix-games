package com.fenixgames.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fenixgames.domain.model.AnimationKind
import com.fenixgames.domain.model.ContentRating
import com.fenixgames.domain.model.GameMode
import com.fenixgames.domain.model.PenaltyPolicy

@OptIn(ExperimentalLayoutApi::class)
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
                    onTeamsChange = viewModel::updateTeams,
                    onCompetition = viewModel::setCompetitionEnabled,
                    onAccept = viewModel::startAcceptedSession
                )
                else -> GamePanel(
                    state = state,
                    onNext = viewModel::nextCard,
                    onScore = viewModel::scoreActor
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SetupPanel(
    state: HomeUiState,
    onPlayersChange: (String) -> Unit,
    onMode: (GameMode) -> Unit,
    onRating: (ContentRating) -> Unit,
    onPenalty: (PenaltyPolicy) -> Unit,
    onTeamsChange: (String) -> Unit,
    onCompetition: (Boolean) -> Unit,
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
        GameMode.entries.forEach { mode ->
            GameModeChip(mode.label, mode, state.selectedMode, onMode)
        }
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
    CompetitionSetup(
        state = state,
        onTeamsChange = onTeamsChange,
        onCompetition = onCompetition
    )
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
    onNext: () -> Unit,
    onScore: (Int) -> Unit
) {
    Text(
        text = "${state.selectedMode.name} / ${state.selectedRating.label}",
        style = MaterialTheme.typography.bodyMedium
    )
    Spacer(Modifier.height(12.dp))
    ModeAnimation(state.selectedMode.animationKind, state.roundIndex)
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
    if (state.competitionEnabled) {
        ScorePanel(state = state, onScore = onScore)
        Spacer(Modifier.height(16.dp))
    }
    Button(
        modifier = Modifier.fillMaxWidth(),
        enabled = state.currentCardText != null,
        onClick = onNext
    ) {
        Text("Siguiente carta")
    }
}

@Composable
private fun CompetitionSetup(
    state: HomeUiState,
    onTeamsChange: (String) -> Unit,
    onCompetition: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            Text("Competencia por equipos", fontWeight = FontWeight.SemiBold)
            Text(
                text = if (state.selectedMode.supportsCompetition) {
                    "Valido para este modo."
                } else {
                    "Este modo no usa competencia."
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
        Switch(
            checked = state.competitionEnabled,
            enabled = state.selectedMode.supportsCompetition,
            onCheckedChange = onCompetition
        )
    }
    if (state.competitionEnabled && state.selectedMode.supportsCompetition) {
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.teamsText,
            onValueChange = onTeamsChange,
            label = { Text("Equipos, separados por coma") }
        )
    }
}

@Composable
private fun ScorePanel(
    state: HomeUiState,
    onScore: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("Puntaje", fontWeight = FontWeight.Bold)
            state.teamScores.forEach { team ->
                Text("${team.name}: ${team.score.points}")
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onScore(1) }) { Text("+1 al turno") }
                Button(onClick = { onScore(-1) }) { Text("-1 al turno") }
            }
        }
    }
}

@Composable
private fun ModeAnimation(kind: AnimationKind, roundIndex: Int) {
    when (kind) {
        AnimationKind.NONE -> Unit
        AnimationKind.ROULETTE -> RouletteAnimation(roundIndex)
        AnimationKind.DICE -> DiceAnimation(roundIndex)
        AnimationKind.CHARADES -> SymbolAnimation("🎭", "Actuar")
        AnimationKind.SECRET -> SymbolAnimation("?", "Secreto")
        AnimationKind.TABOO -> SymbolAnimation("!", "Tabú")
    }
}

@Composable
private fun RouletteAnimation(roundIndex: Int) {
    val transition = rememberInfiniteTransition(label = "roulette")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f + (roundIndex * 17f),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200),
            repeatMode = RepeatMode.Restart
        ),
        label = "rouletteRotation"
    )
    Canvas(
        modifier = Modifier
            .size(112.dp)
            .rotate(rotation)
    ) {
        val radius = size.minDimension / 2.5f
        drawCircle(
            color = Color(0xFF006B5F),
            radius = radius,
            style = Stroke(width = 10f)
        )
        drawLine(
            color = Color(0xFFFFC35A),
            start = center,
            end = Offset(center.x, center.y - radius),
            strokeWidth = 12f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun DiceAnimation(roundIndex: Int) {
    val face = (roundIndex % 6) + 1
    Box(
        modifier = Modifier
            .size(72.dp)
            .background(MaterialTheme.colorScheme.primaryContainer),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
            text = face.toString(),
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SymbolAnimation(symbol: String, label: String) {
    Text(
        text = "$symbol $label",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun ChipRows(content: @Composable () -> Unit) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
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
