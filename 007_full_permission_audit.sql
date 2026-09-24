-- Safety net: even after the app fix (sending the real login token), grant
-- merchant_staff read access to anon too. RLS still restricts it to "your
-- own staff row only" (auth.uid() = user_id), so this can't leak data —
-- it just stops a missing base-table grant from ever causing a hard error
-- again for any future feature that references merchant_staff.
grant select on public.merchant_staff to anon;
