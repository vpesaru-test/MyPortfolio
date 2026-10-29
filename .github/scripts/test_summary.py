"""Turn TestNG results into a GitHub Actions job summary plus an error annotation per failure.

Usage: python3 test_summary.py <testng-results.xml> <browser>
"""
import os
import sys
import xml.etree.ElementTree as ET


def esc(text):
    """Escape a value for a ::error workflow command."""
    return text.replace("%", "%25").replace("\r", "%0D").replace("\n", "%0A")


def main():
    path, browser = sys.argv[1], sys.argv[2]
    summary_file = os.environ.get("GITHUB_STEP_SUMMARY")
    if not os.path.exists(path):
        print(f"::error title=UI tests ({browser})::No TestNG results found - the build failed before tests ran")
        return

    root = ET.parse(path).getroot()
    counts = {k: int(root.get(k, 0)) for k in ("total", "passed", "failed", "skipped")}
    failures = []
    for cls in root.iter("class"):
        for method in cls.iter("test-method"):
            if method.get("is-config") == "true" or method.get("status") != "FAIL":
                continue
            message = ""
            exc = method.find("exception")
            if exc is not None:
                msg = exc.find("message")
                message = (msg.text or "").strip() if msg is not None else exc.get("class", "")
            params = [p.findtext("value", "").strip() for p in method.iter("param")]
            name = cls.get("name").rsplit(".", 1)[-1] + "." + method.get("name")
            if params:
                name += "(" + ", ".join(params[:2]) + ")"
            failures.append((name, message))

    for name, message in failures:
        print(f"::error title={esc(browser + ': ' + name)}::{esc(message[:1500])}")

    lines = [
        f"### UI tests — {browser}",
        "",
        f"**{counts['passed']} passed**, **{counts['failed']} failed**, {counts['skipped']} skipped "
        f"(of {counts['total']})",
        "",
    ]
    if failures:
        lines += ["| Test | Reason |", "|---|---|"]
        for name, message in failures:
            first_line = message.splitlines()[0] if message else ""
            reason = first_line.replace("|", "\\|")[:300]
            lines.append(f"| `{name}` | {reason} |")
    if summary_file:
        with open(summary_file, "a", encoding="utf-8") as fh:
            fh.write("\n".join(lines) + "\n")
    print("\n".join(lines))


if __name__ == "__main__":
    main()
