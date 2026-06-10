<script lang="ts">
	import { getContext } from 'svelte';

	let userContext = getContext<any>('user');
	let storeContext = getContext<any>('store');

	let user = $derived(userContext.get());
	let selectedStoreId = $derived(storeContext.getSelected());
</script>

{#if user}
	<div class="container py-4">
		<div class="row">
			<div class="col-12">
				<div class="card p-5 mb-4 bg-light rounded-3 shadow-sm border-0">
					<div class="container-fluid py-5">
						<h1 class="display-5 fw-bold text-dark mb-3">Welcome back, {user.firstName}!</h1>
						<p class="col-md-8 fs-4 text-muted">
							Manage rentals, customers, and inventory for your designated store location.
						</p>
						<hr class="my-4" />

						{#if selectedStoreId !== null}
							<div class="d-flex align-items-center gap-3">
								<div
									class="p-3 bg-primary text-white rounded-circle shadow-sm d-flex align-items-center justify-content-center"
									style="width: 55px; height: 55px;"
								>
									<span class="fs-4 fw-bold">{selectedStoreId}</span>
								</div>
								<div>
									<h4 class="mb-0 text-secondary">Active Store Location</h4>
									<p class="mb-0 text-muted">Currently managing Store ID #{selectedStoreId}</p>
								</div>
							</div>
						{:else}
							<p class="text-warning">
								No store location selected. Please select a store from the navigation bar.
							</p>
						{/if}
					</div>
				</div>
			</div>
		</div>
	</div>
{/if}
