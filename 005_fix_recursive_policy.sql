drop policy "staff view own staff list" on public.merchant_staff;

create policy "staff view own staff list" on public.merchant_staff
  for select using (auth.uid() = user_id);
