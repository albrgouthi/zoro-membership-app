# Zoro — Discount & Membership App

MVP build. Connects to Supabase (Postgres + Auth) for data, built with
Kotlin + Jetpack Compose (Material 3).

## What's in this first version
- Home screen pulling live categories from Supabase, with an EN/ES/AR language toggle
- Database schema with Row Level Security (see `001_init_schema.sql` in the project chat history — apply it in Supabase's SQL Editor)
- Automated APK build via GitHub Actions on every push to `main`

## Getting the APK
Every push to `main` triggers a build. Find the finished `.apk` under:
**Actions tab → latest run → Artifacts → zoro-debug-apk**

## Not yet included (next milestones)
- Login / signup (email + phone OTP)
- Digital membership card with rotating QR
- Merchant dashboard & offer creation
- Stripe subscription payments
- Redemption scanner
