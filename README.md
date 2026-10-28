# Vidyullatha Pesaru — Portfolio

Personal portfolio site for a Senior QA Engineer & Test Automation Architect.
Built with [Jekyll](https://jekyllrb.com/), which GitHub Pages runs automatically:
content lives in plain YAML files, and the page is generated from them on every push.

## Where things live

| To change…                                   | Edit this file                  |
|----------------------------------------------|---------------------------------|
| Name, headline, summary, badges, stats, contact | `_data/profile.yml`          |
| Featured projects                            | `_data/projects.yml`            |
| Work history                                 | `_data/experience.yml`          |
| Skills                                       | `_data/skills.yml`              |
| Certifications                               | `_data/certifications.yml`      |
| Education                                    | `_data/education.yml`           |
| Profile photo                                | `assets/images/profile.jpg`     |
| Colours, fonts, spacing                      | `assets/css/style.css`          |
| Page layout / section order                  | `index.html`                    |
| Markup for a single section                  | `_includes/<section>.html`      |

Most updates only touch `_data/`. Each data file has comments at the top
explaining its fields.

```
.
├── _config.yml            Jekyll settings
├── _data/                 ← your content
│   ├── profile.yml
│   ├── projects.yml
│   ├── experience.yml
│   ├── skills.yml
│   ├── certifications.yml
│   └── education.yml
├── _includes/             one template per section
├── _layouts/default.html  page shell (<head>, CSS, JS)
├── assets/
│   ├── css/style.css
│   ├── js/main.js         scroll fade-in animation
│   └── images/profile.jpg
└── index.html             assembles the sections in order
```

## Common edits

**Add a job** — add an entry at the top of `_data/experience.yml`:

```yaml
- company: Acme Corp
  location: Austin, TX
  role: Lead QA Engineer
  period: Jan 2027 — Present
  description: >-
    One paragraph on scope and impact.
  tags:
    primary: [Bruno, Java]       # orange pills
    secondary: [GitLab CI]       # teal pills
    other: [Docker]              # neutral pills
```

**Add a certification** — add to `_data/certifications.yml` with
`kind: ai | aws | course | professional` (controls the dot colour) and an
optional `url` to the credential.

**Add a project** — follow the commented example in `_data/projects.yml`.
The Projects section and its nav link appear automatically once the list has
an entry, and the other sections renumber themselves.

YAML tips: indentation must be spaces, not tabs. Wrap a value in quotes if it
contains a colon (`"AI Fluency: Framework & Foundations"`).

## Publishing

1. Push to the default branch.
2. One time only: in the repo, go to **Settings → Pages**, set
   **Source** to *Deploy from a branch*, and choose the default branch and
   `/ (root)`.
3. The site is published at `https://<username>.github.io/<repo>/` a minute
   or two after each push. Build errors show under the repo's **Actions** tab.

## Previewing locally (optional)

Requires Ruby. From the repo folder:

```bash
bundle install
bundle exec jekyll serve
```

Then open http://localhost:4000. Opening `index.html` directly in a browser
will **not** work, because the page has to be generated first.
