-- Run this in Supabase SQL Editor. Without this, RLS policies exist but the
-- anon role has no baseline permission to touch the tables at all.

grant usage on schema public to anon, authenticated;

grant select on public.categories to anon, authenticated;
grant select on public.membership_plans to anon, authenticated;
grant select on public.merchants to anon, authenticated;
grant select on public.branches to anon, authenticated;
grant select on public.offers to anon, authenticated;

grant select, insert, update, delete on public.profiles to authenticated;
grant select, insert, update, delete on public.subscriptions to authenticated;
grant select, insert, update, delete on public.redemptions to authenticated;
grant select, insert, update, delete on public.favorites to authenticated;
grant select, update on public.merchant_staff to authenticated;
