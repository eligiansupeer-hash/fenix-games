import collections
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
CARDS = ROOT / "app" / "src" / "main" / "assets" / "cards.json"

EXPECTED_MODES = {
    "NEVER_HAVE_I_EVER",
    "TRUTH_OR_DARE",
    "QUESTIONS",
    "ROULETTE",
    "PREVIA",
    "ARGENTO",
    "CHARADES",
    "IMPOSTOR",
    "TABOO",
    "TRIVIA",
}

EXPECTED_RATINGS = [
    "TEEN",
    "ADULT_1",
    "ADULT_2",
    "ADULT_3",
    "ADULT_4",
    "ADULT_5",
    "ADULT_6",
]

EXPECTED_CARD_TYPES = {
    "TRUTH",
    "DARE",
    "QUESTION",
    "ROULETTE",
    "CHALLENGE",
    "SECRET",
    "TRIVIA",
}

FORBIDDEN = re.compile(
    r"\b(instagram|whatsapp|estado|estados|mensaje|mensajes|foto|fotos|redes|celular|elige|elegi|escoge|escoge)\b",
    re.IGNORECASE,
)

BAD_COPY = re.compile(
    r"(\ufffd|yo nunca admit|me daria curiosidad|personas externas|fuera del grupo)",
    re.IGNORECASE,
)


def main() -> int:
    pack = json.loads(CARDS.read_text(encoding="utf-8"))
    errors: list[str] = []
    if not pack.get("name"):
        errors.append("pack is missing required name")
    cards = pack["cards"]
    modes = {card["mode"] for card in cards}
    if modes != EXPECTED_MODES:
        errors.append(f"Modes mismatch: {sorted(modes)}")

    counts = collections.Counter((card["mode"], card["rating"]) for card in cards)
    for mode in sorted(EXPECTED_MODES):
        for rating in EXPECTED_RATINGS:
            count = counts[(mode, rating)]
            if count != 20:
                errors.append(f"{mode}/{rating} has {count} cards, expected 20")

    for card in cards:
        text = card["textTemplate"]
        if card["cardType"] not in EXPECTED_CARD_TYPES:
            errors.append(f"{card['id']} has invalid cardType: {card['cardType']}")
        if FORBIDDEN.search(text):
            errors.append(f"{card['id']} contains forbidden external/social wording: {text}")
        if BAD_COPY.search(text):
            errors.append(f"{card['id']} contains broken or weak copy: {text}")
        target_policy = card["targetPolicy"]
        if target_policy == "ONE_TARGET" and "{target}" not in text:
            errors.append(f"{card['id']} missing {{target}}")
        if target_policy == "TWO_TARGET_OPTIONS" and (
            "{targetA}" not in text or "{targetB}" not in text
        ):
            errors.append(f"{card['id']} missing {{targetA}}/{{targetB}}")
        if target_policy == "NONE" and any(token in text for token in ("{target}", "{targetA}", "{targetB}")):
            errors.append(f"{card['id']} has target placeholders but targetPolicy NONE")

    print(f"cards={len(cards)} modes={len(modes)}")
    if errors:
        for error in errors:
            print(f"ERROR: {error}", file=sys.stderr)
        return 1
    print("cards validation OK")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
