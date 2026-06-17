<script lang="ts">
	import favicon from '$lib/assets/favicon.svg';
	import { onMount, setContext } from 'svelte';
	import { goto } from '$app/navigation';
	import type { Store, User } from 'shared';

	let { children } = $props();
	let user = $state<User | null>(null);
	let stores = $state<Store[]>([]);
	let selectedStoreId = $state<number | null>(null);

	setContext('user', {
		get: () => user,
		set: (newUser: any) => (user = newUser)
	});

	setContext('store', {
		getStores: () => stores,
		getSelected: () => selectedStoreId,
		setSelected: (id: number) => (selectedStoreId = id)
	});

	onMount(() => {
		if (!user) {
			goto('/login');
		}
	});

	async function fetchStores(token: string) {
		try {
			const response = await fetch('/api/stores', {
				headers: {
					Authorization: `Bearer ${token}`
				}
			});
			if (response.ok) {
				const data: Store[] = await response.json();
				stores = data;
				if (data.length > 0) {
					selectedStoreId = data[0].storeId;
				}
			} else {
				console.error('Failed to fetch stores: status', response.status);
			}
		} catch (error) {
			console.error('Failed to fetch stores', error);
		}
	}

	$effect(() => {
		if (user) {
			fetchStores(user.token);
		} else {
			stores = [];
			selectedStoreId = null;
		}
	});
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
</svelte:head>

{#if user}
	<nav class="navbar navbar-expand-lg navbar-dark bg-dark px-4 py-2 mb-4 shadow">
		<div class="container-fluid d-flex justify-content-between align-items-center">
			<a class="navbar-brand d-flex align-items-center gap-2" href="/">
				<span class="fs-4 fw-bold text-gradient">Sakila Store</span>
			</a>

			<div class="d-flex align-items-center gap-3">
				{#if stores.length > 0}
					<div class="d-flex align-items-center gap-2">
						<label for="storeSelector" class="text-white-50 small mb-0 text-nowrap"
						>Active Store:</label
						>
						<select
							id="storeSelector"
							class="form-select form-select-sm bg-secondary text-white border-0 select-custom"
							onchange={(e) => (selectedStoreId = parseInt(e.currentTarget.value))}
						>
							{#each stores as store}
								<option value={store.storeId} selected={store.storeId === selectedStoreId}>Store #{store.storeId}
									– {store.address}</option>
							{/each}
						</select>
					</div>
				{/if}

				<div class="text-white small border-start ps-3 border-secondary">
					Logged in as: <strong class="text-info">{user.firstName} {user.lastName}</strong>
				</div>
			</div>
		</div>
	</nav>
{/if}

<div class="content">
	{@render children()}
</div>

<style>
    .content {
        padding: 1rem;
    }

    .text-gradient {
        background: linear-gradient(45deg, #0d6efd, #0dcaf0);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
    }

    .select-custom {
        width: 16rem;
        cursor: pointer;
        font-weight: 500;
        transition: background-color 0.2s ease;
    }

    .select-custom:hover {
        background-color: #495057 !important;
    }

    .select-custom:focus {
        box-shadow: 0 0 0 0.25rem rgba(13, 202, 240, 0.25);
        border-color: #0dcaf0;
    }
</style>
