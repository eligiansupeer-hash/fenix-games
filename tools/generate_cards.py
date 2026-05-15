import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CARDS = ROOT / "app" / "src" / "main" / "assets" / "cards.json"

RATINGS = ["TEEN", "ADULT_1", "ADULT_2", "ADULT_3", "ADULT_4", "ADULT_5", "ADULT_6"]

MODES = {
    "NEVER_HAVE_I_EVER": ("never", "TRUTH"),
    "TRUTH_OR_DARE": ("truth_dare", "DARE"),
    "QUESTIONS": ("question", "QUESTION"),
    "ROULETTE": ("roulette", "ROULETTE"),
    "PREVIA": ("previa", "CHALLENGE"),
    "ARGENTO": ("argento", "CHALLENGE"),
    "CHARADES": ("charades", "CHALLENGE"),
    "IMPOSTOR": ("impostor", "SECRET"),
    "TABOO": ("taboo", "CHALLENGE"),
    "TRIVIA": ("trivia", "TRIVIA"),
}

LEVEL_LINES = {
    "TEEN": [
        "confesar quien tiene mas cara de chamuyo inocente en la ronda",
        "hacer una imitacion de cita fallida durante diez segundos",
        "decir que gesto de la ronda da mas ternura y cual da mas verguenza",
        "sostener una mirada seria hasta que alguien se ria",
        "contar un bardo liviano sin nombrar a nadie ausente",
        "inventar un apodo amistoso para esta ronda",
        "admitir que frase usaria para cortar el hielo",
        "hacer una pose de ganador como si acabara de conquistar la mesa",
        "decir que jugador parece mas dificil de impresionar",
        "defender una opinion ridicula con total seguridad",
    ],
    "ADULT_1": [
        "sostener una mirada con intencion durante diez segundos",
        "decir que detalle de actitud vuelve interesante a alguien",
        "confesar cuando sintio tension en una juntada parecida",
        "hacer un brindis breve con tono seductor",
        "describir una cita ideal sin salir de esta habitacion",
        "decir que tipo de voz le resulta mas atractiva",
        "contar cual fue su chamuyo mas efectivo",
        "marcar con una frase donde empieza la tension",
        "hacer un cumplido adulto, directo y elegante",
        "admitir que gesto le despierta curiosidad",
    ],
    "ADULT_2": [
        "decir al oido una frase de tension sin tocar",
        "quedarse a un palmo de distancia respirando lento cinco segundos",
        "confesar que situacion presencial le prende la cabeza",
        "describir como subiria la temperatura cuidando limites",
        "jugar una provocacion verbal intensa sin usar objetos externos",
        "decir que mirada lo dejaria pensando",
        "hacer una propuesta picante que pueda rechazarse sin drama",
        "marcar una frontera clara antes de subir el tono",
        "confesar que tipo de cercania le cuesta ignorar",
        "responder una pregunta incomoda sin escapar con chistes",
    ],
    "ADULT_3": [
        "dar un beso breve en la mejilla si ambas personas aceptan",
        "acariciar la mano durante diez segundos con contacto visual",
        "bailar cerca cuidando el espacio aceptado",
        "rozar hombros y sostener la cercania una ronda",
        "acercarse hasta que la tension se note y separarse sin besar",
        "pedir permiso para una caricia leve en brazo u hombro",
        "susurrar una frase picante y volver a su lugar",
        "quedarse frente a frente durante cinco segundos",
        "marcar una escena de beso sin llegar a la boca",
        "hacer una pregunta sobre deseo sin exigir respuesta",
    ],
    "ADULT_4": [
        "pedir permiso para una caricia mas atrevida en cintura u hombros",
        "dar un beso mas largo en la mejilla o cerca de la boca sin forzar",
        "marcar una escena de tension corporal fuerte con distancia minima",
        "guiar las manos de la otra persona solo donde haya acuerdo claro",
        "hacer una confesion de deseo con la ronda escuchando en silencio",
        "quitar una prenda externa solo si la regla fue aceptada",
        "bailar pegado durante diez segundos con permiso explicito",
        "decir donde termina el juego y empieza el limite personal",
        "sostener una caricia lenta en hombros o espalda alta",
        "proponer un beso y aceptar la respuesta sin insistir",
    ],
    "ADULT_5": [
        "describir una fantasia intima sin nombrar a nadie fuera de la ronda",
        "decir que contacto adulto le resultaria imposible ignorar esta noche",
        "jugar una escena de dormitorio verbal sin tocar zonas intimas",
        "permitir que la otra persona indique una prenda externa o pagar penalizacion",
        "sostener una caricia intensa solo donde haya consentimiento claro",
        "confesar que tipo de placer le gusta provocar",
        "decir una frase de deseo mirando fijo",
        "proponer una pausa para acordar limites antes de subir el nivel",
        "describir un beso que no se olvida",
        "marcar una invitacion intima que pueda rechazarse sin presion",
    ],
    "ADULT_6": [
        "expresar una propuesta sexual adulta y consensuada, con limites claros",
        "decir que placer sexual buscaria dar y que permiso pediria primero",
        "describir una escena intima directa con acuerdos y cuidado",
        "preguntar que limite sexual debe quedar explicito antes de avanzar",
        "plantear una invitacion sexual directa que la otra persona puede aceptar o rechazar",
        "decir que acuerdo haria falta antes de cualquier contacto sexual",
        "nombrar una fantasia sexual adulta sin presionar a nadie",
        "proponer una regla de consentimiento para seguir jugando en este nivel",
        "decir que practica sexual no haria sin hablarlo antes",
        "marcar una pausa clara para confirmar deseo, cuidado y limites",
    ],
}


def target_policy(mode: str, index: int) -> str:
    if mode in {"TRIVIA", "TABOO", "CHARADES"}:
        return "NONE" if index % 4 in {0, 3} else "ONE_TARGET"
    if index % 5 == 4:
        return "TWO_TARGET_OPTIONS"
    if index % 3 == 2:
        return "NONE"
    return "ONE_TARGET"


def with_target(base: str, policy: str) -> str:
    if policy == "ONE_TARGET":
        return f"{base} con {{target}}"
    if policy == "TWO_TARGET_OPTIONS":
        return f"{base} entre {{targetA}} y {{targetB}}"
    return base


def text_for(mode: str, rating: str, index: int) -> tuple[str, str]:
    line = LEVEL_LINES[rating][index % len(LEVEL_LINES[rating])]
    policy = target_policy(mode, index)
    line = with_target(line, policy)

    if mode == "NEVER_HAVE_I_EVER":
        return f"{{actor}}: Yo nunca tuve que {line}. Quien si, paga una ronda de puntos.", "TRUTH"
    if mode == "TRUTH_OR_DARE":
        kind = "Verdad" if index % 2 == 0 else "Reto"
        prompt = "responde sin vueltas" if kind == "Verdad" else "hazlo o toma la penalizacion"
        return f"{kind}: {{actor}}, {prompt}: {line}.", "TRUTH" if kind == "Verdad" else "DARE"
    if mode == "QUESTIONS":
        return f"Pregunta para {{actor}}: que pasaria si ahora tuviera que {line}?", "QUESTION"
    if mode == "ROULETTE":
        return f"Ruleta: {{actor}}, la ronda marca que toca {line}.", "ROULETTE"
    if mode == "PREVIA":
        return f"Previa: {{actor}} suma energia a la mesa y debe {line}.", "CHALLENGE"
    if mode == "ARGENTO":
        return f"Argento salvaje: {{actor}}, bancatela con estilo y manda esta: {line}.", "CHALLENGE"
    if mode == "CHARADES":
        return f"Mimica: {{actor}} representa sin hablar la idea de {line}. El grupo adivina.", "CHALLENGE"
    if mode == "IMPOSTOR":
        return f"Secreto de ronda: {{actor}} recibe la pista '{line}'. El impostor debe disimular.", "SECRET"
    if mode == "TABOO":
        return f"Tabu: {{actor}} hace adivinar '{line}'. Prohibido decir las palabras principales de la frase.", "CHALLENGE"
    if mode == "TRIVIA":
        return f"Trivia rapida: {{actor}}, que jugador de la ronda seria mas capaz de {line}? Defiende la respuesta en diez segundos.", "TRIVIA"
    raise ValueError(mode)


def main() -> None:
    cards = []
    for mode, (_, default_type) in MODES.items():
        for rating_rank, rating in enumerate(RATINGS):
            for index in range(20):
                text, card_type = text_for(mode, rating, index)
                cards.append(
                    {
                        "id": f"{mode.lower()}-{rating.lower()}-{index + 1:02d}",
                        "packId": "base-presential-pack",
                        "mode": mode,
                        "rating": rating,
                        "ratingRank": rating_rank,
                        "cardType": card_type or default_type,
                        "category": f"{mode.lower()}-{rating.lower()}",
                        "textTemplate": text,
                        "targetPolicy": target_policy(mode, index),
                        "penaltyPolicy": "LOSE_POINT",
                    }
                )

    pack = {
        "id": "base-presential-pack",
        "name": "Fenix Games presencial",
        "version": 3,
        "cards": cards,
    }
    CARDS.write_text(json.dumps(pack, ensure_ascii=True, indent=2) + "\n", encoding="utf-8")


if __name__ == "__main__":
    main()
