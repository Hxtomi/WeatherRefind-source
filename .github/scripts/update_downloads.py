import json
import re
import xml.etree.ElementTree as ET
from pathlib import Path
from urllib.parse import quote
from urllib.request import Request, urlopen

PROJECT_IDS = ("Qyzsewil",)
CURSEFORGE_ID = 1500089
CREATED = "Mar 31, 2026"
USER_AGENT = "Hxtomi/WeatherRefind-source (GitHub download badges)"
BADGES = Path(__file__).resolve().parents[1] / "badges"
WIDTH, HEIGHT, LABEL_WIDTH = 160, 20, 75
NS = {"svg": "http://www.w3.org/2000/svg"}


def fetch(url):
    request = Request(url, headers={"User-Agent": USER_AGENT})
    with urlopen(request, timeout=30) as response:
        return response.read()


def fixed_badge(svg, label, expected_value=None):
    badge = ET.fromstring(svg)
    aria = badge.get("aria-label", "")
    prefix = f"{label}: "
    if badge.tag != f"{{{NS['svg']}}}svg" or badge.get("height") != str(HEIGHT) or not aria.startswith(prefix):
        raise ValueError("Unexpected Shields badge response")
    value = aria[len(prefix):]
    if expected_value is not None:
        if value != expected_value:
            raise ValueError("Unexpected badge value")
    elif not re.fullmatch(r"\d+(?:\.\d+)?[kMBT]?", value):
        raise ValueError("Unexpected download count")

    clip = badge.find("svg:clipPath/svg:rect", NS)
    backgrounds = badge.find("svg:g[@clip-path='url(#r)']", NS)
    text = badge.find("svg:g[@font-size='110']", NS)
    title = badge.find("svg:title", NS)
    if clip is None or backgrounds is None or text is None or title is None:
        raise ValueError("Unexpected badge layout")
    rectangles = backgrounds.findall("svg:rect", NS)
    groups = text.findall("svg:g", NS)
    if len(rectangles) != 3 or len(groups) != 2:
        raise ValueError("Unexpected badge sections")

    badge.set("width", str(WIDTH))
    badge.set("viewBox", f"0 0 {WIDTH} {HEIGHT}")
    clip.set("width", str(WIDTH))
    rectangles[0].set("width", str(LABEL_WIDTH))
    rectangles[1].set("x", str(LABEL_WIDTH))
    rectangles[1].set("width", str(WIDTH - LABEL_WIDTH))
    rectangles[2].set("width", str(WIDTH))
    for group, center, available in (
        (groups[0], LABEL_WIDTH / 2, LABEL_WIDTH - 10),
        (groups[1], (LABEL_WIDTH + WIDTH) / 2, WIDTH - LABEL_WIDTH - 10),
    ):
        for item in group.findall(".//svg:text", NS):
            if float(item.get("textLength", "0")) / 10 > available:
                raise ValueError("Badge text does not fit")
            item.set("x", f"{center * 10:g}")
    return badge


def main():
    total = 0
    for project_id in PROJECT_IDS:
        project = json.loads(fetch(f"https://api.modrinth.com/v2/project/{project_id}"))
        count = project["downloads"]
        if project["id"] != project_id or type(count) is not int or count < 0:
            raise ValueError("Unexpected Modrinth project response")
        total += count

    count_label = str(total)
    for divisor, suffix in ((1_000_000_000, "B"), (1_000_000, "M"), (1_000, "k")):
        if total >= divisor:
            value = total / divisor
            count_label = f"{value:.1f}".rstrip("0").rstrip(".") if value < 10 else f"{value:.0f}"
            count_label += suffix
            break

    sources = {
        "curseforge": (f"https://img.shields.io/curseforge/dt/{CURSEFORGE_ID}?label=CurseForge&color=F16436&style=flat", "CurseForge", None),
        "modrinth": (f"https://img.shields.io/badge/Modrinth-{count_label}-177c47?style=flat", "Modrinth", count_label),
        "created": (f"https://img.shields.io/badge/Created-{quote(CREATED, safe='')}-54748D?style=flat", "Created", CREATED),
        "modpack-friendly": ("https://img.shields.io/badge/Modpack-Friendly-8FAF9A?style=flat", "Modpack", "Friendly"),
    }
    # Fetch and validate everything before changing any existing badge.
    rendered = {}
    ET.register_namespace("", NS["svg"])
    for name, (url, label, expected_value) in sources.items():
        badge = fixed_badge(fetch(url), label, expected_value)
        if name == "modrinth":
            badge.set("aria-label", f"Modrinth: {total:,} downloads")
            combined = ", both projects combined" if len(PROJECT_IDS) > 1 else ""
            badge.find("svg:title", NS).text = f"Modrinth: {total:,} downloads{combined}"
        rendered[name] = ET.tostring(badge, encoding="unicode") + "\n"

    BADGES.mkdir(parents=True, exist_ok=True)
    for name, svg in rendered.items():
        (BADGES / f"{name}.svg").write_text(svg, encoding="utf-8", newline="\n")
    print(f"Updated {len(rendered)} badges at {WIDTH}x{HEIGHT}; Modrinth downloads: {total}")


if __name__ == "__main__":
    main()
