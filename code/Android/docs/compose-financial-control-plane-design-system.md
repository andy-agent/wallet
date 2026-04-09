# Android Compose Financial Control Plane Design System

## Scope

This design system freezes the Android/Compose visual language for the white-base financial control plane.

- Base atmosphere: white and near-white surfaces, not dark-market neon.
- Semantic accents:
  - Infra: low blue
  - Settlement and safety: restrained green
  - Finance: small, deliberate purple
- Explicit audit states: `ok`, `warn`, `critical`, `unknown`
- Explicit depth model: layer `0/1/2/3`
- Motion tone: trustworthy, quiet, system-led

Primary implementation entrypoints:

- `composeui/theme/ControlPlaneDesignSystem.kt`
- `composeui/theme/Color.kt`
- `composeui/theme/Theme.kt`
- `composeui/components/cards/BaseCard.kt`
- `composeui/components/tags/StatusTag.kt`

## Color Semantics

| Semantic | Compose token | Hex | Usage |
| --- | --- | --- | --- |
| White base | `ControlPlaneTokens.layer(Level0).container` | `#FFFFFF` | App canvas, full-screen background |
| Layer 1 | `ControlPlaneTokens.layer(Level1).container` | `#FBFCFE` | Standard cards, list rows |
| Layer 2 | `ControlPlaneTokens.layer(Level2).container` | `#F6F9FC` | Emphasized panels, grouped regions |
| Layer 3 | `ControlPlaneTokens.layer(Level3).container` | `#F2F6FA` | Sheets, dialogs, floating overlays |
| Infra blue | `ControlPlaneTokens.Infra.accent` | `#5F7FA6` | Structure, connectivity, tools, system actions |
| Settlement green | `ControlPlaneTokens.Settlement.accent` | `#4E8872` | Confirmed, safe, settled, available |
| Finance purple | `ControlPlaneTokens.Finance.accent` | `#786EAD` | Money movement, yield, pricing emphasis |
| Ink | `ControlPlaneTokens.Ink` | `#162231` | Primary text and dense controls |
| Secondary ink | `ControlPlaneTokens.InkSecondary` | `#536375` | Support copy and metadata |
| Tertiary ink | `ControlPlaneTokens.InkTertiary` | `#7C8A99` | Hints, secondary labels, quiet chrome |

Rules:

- Default screens start from white base plus layers, not gradients.
- Infra blue is structural. Do not use it to communicate success or profit.
- Settlement green is reserved for safe completion, healthy state, or protective actions.
- Finance purple is an accent, not the dominant shell color. Use it in small concentrations.

## Audit State Semantics

| State | Compose token | Visual rule | Meaning |
| --- | --- | --- | --- |
| `ok` | `ControlPlaneTokens.audit(AuditState.Ok)` | Green dot, green-tinted surface, low-contrast border | Healthy, confirmed, reconciled |
| `warn` | `ControlPlaneTokens.audit(AuditState.Warn)` | Amber indicator, warm surface, no red | Needs attention, pending risk, waiting window |
| `critical` | `ControlPlaneTokens.audit(AuditState.Critical)` | Red indicator and border, pale red surface | Broken, failed, blocked, unreconciled |
| `unknown` | `ControlPlaneTokens.audit(AuditState.Unknown)` | Slate indicator and neutral surface | Unverified, syncing, hidden, stale data |

Rules:

- Never use green or purple for `warn`.
- Never use purple for audit severity.
- `unknown` must look incomplete, not healthy.
- Status surfaces stay pale. Severity should read from indicator, border, and text hierarchy before fill intensity.

## Layer System

| Layer | Compose token | Elevation | Intended components |
| --- | --- | --- | --- |
| `0` | `ControlPlaneTokens.layer(Level0)` | `0dp` | Screen canvas, scroll background |
| `1` | `ControlPlaneTokens.layer(Level1)` | `2dp` | Standard cards, list cells, inline panels |
| `2` | `ControlPlaneTokens.layer(Level2)` | `6dp` | Pinned summaries, highlighted sections, composite controls |
| `3` | `ControlPlaneTokens.layer(Level3)` | `10dp` | Modal sheets, dialogs, system overlays |

Rules:

- Layers are communicated by subtle surface drift and outline strength, not dramatic shadow.
- Do not skip from `0` to `3` for ordinary cards.
- When two surfaces touch, the higher layer keeps the stronger outline.

## Motion Rules

Motion tokens are defined in `ControlPlaneTokens.Motion`.

| Motion | Token | Duration | Rule |
| --- | --- | --- | --- |
| State change | `stateChange` | `180ms` | Use for status flips and inline validation |
| Screen enter | `screenEnter` | `260ms` | Use for page transitions and large content handoff |
| Emphasis | `emphasis` | `220ms` | Use for quiet highlight shifts and focused reveal |
| Settlement confirmation | `settlementConfirmation` | `320ms` + `40ms` delay | Use for completion acknowledgement and safe money movement |

Motion tone rules:

- Start fast, end soft.
- Prefer fade, slight slide, or tint wash.
- Keep live animated effects to one per viewport.
- Keep glow alpha at or below `0.08`.
- Avoid perpetual loops on data-heavy screens unless they communicate real system state.

## Translating Cinematic Requests Into Compose-Safe Rules

| Cinematic request | Compose-safe equivalent |
| --- | --- |
| “Make it feel like a control room” | Use `Level1/2` surfaces, strong ink hierarchy, infra-blue structural accents |
| “Add atmosphere” | Add one low-alpha radial or vertical tint wash behind the screen, not multiple animated glows |
| “Show energy / data flow” | Use directional `180ms` highlight movement on state change, not particles |
| “Make settlement feel satisfying” | Use the `settlementConfirmation` token with green accent and stable number transition |
| “More futuristic finance” | Add small finance-purple emphasis to key value chips or rate cards only |
| “Glass / cinematic depth” | Use `Level3` surface, outline, and restrained elevation instead of blur-heavy translucency |

Hard constraints:

- No particle fields as the default shell treatment.
- No neon purple backgrounds.
- No dark-mode inversion for this spec. The frozen baseline is white-base.
- No large animated gradients behind ledgers, balances, or audit-critical tables.

## Component Vocabulary

Compose components should converge on this vocabulary:

- Top app bar
- Section header
- Summary card
- Ledger row
- Status tag
- Primary system button
- Secondary outline button
- Money movement CTA
- Filter chip
- Search/input field
- Inline audit banner
- Modal sheet
- Confirmation dialog
- Data table or comparison row

Rules:

- `StatusTag` is the canonical compact audit state component.
- Standard information cards should use `BaseCard` and a `Level1` surface.
- Money movement CTAs may use finance purple only when the action is directly financial.
- Infra actions such as route, sync, region, or network control remain blue.

## Financial-To-Visual Mapping

| Financial concept | Visual language |
| --- | --- |
| Infrastructure, routing, network, region | Infra blue accent, white or layer surface |
| Settlement completed, available balance, verified safety | Settlement green accent with pale green container |
| Pricing, yield, payout rate, asset emphasis | Small finance purple accent with neutral or white surface |
| Pending review, reconciliation gap, expiry window | Warn palette |
| Failed payment, broken sync, blocked withdrawal | Critical palette |
| Unknown quote, stale balance, hidden compliance result | Unknown palette |

## Implementation Notes

- `Color.kt` now aliases legacy color names onto the control-plane semantics so existing Compose screens can migrate without a mass rename.
- `CryptoVPNTheme` is intentionally frozen onto the white-base control-plane scheme until a separate dark-language spec exists.
- Follow-on screen tasks should consume `ControlPlaneTokens` directly for new work instead of inventing new page-local color constants.
