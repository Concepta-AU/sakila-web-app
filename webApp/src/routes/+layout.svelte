<script lang="ts">
	import favicon from '$lib/assets/favicon.svg';
	import { onMount, setContext } from 'svelte';
	import { goto } from '$app/navigation';
	import type { Film, Store, User } from 'shared';

	let { children } = $props();
	let user = $state<User | null>(null);
	let stores = $state<Store[]>([]);
	let selectedStoreId = $state<number | null>(null);

	let searchQuery = $state('');
	let searchResults = $state<Film[]>([]);
	let searchOpen = $state(false);
	let searchDebounce: ReturnType<typeof setTimeout> | null = null;

	async function fetchFilms(query: string) {
		if (!user) return;
		try {
			const response = await fetch(`/api/films/search?title=${encodeURIComponent(query)}`, {
				headers: { Authorization: `Bearer ${user.token}` }
			});
			if (response.ok) {
				searchResults = await response.json();
				searchOpen = searchResults.length > 0;
			}
		} catch {
			searchResults = [];
		}
	}

	function onSearchInput() {
		if (searchDebounce) clearTimeout(searchDebounce);
		if (searchQuery.trim().length === 0) {
			searchResults = [];
			searchOpen = false;
			return;
		}
		searchDebounce = setTimeout(() => fetchFilms(searchQuery.trim()), 250);
	}

	function selectFilm(film: Film) {
		searchQuery = '';
		searchResults = [];
		searchOpen = false;
		goto(`/films/${film.filmId}?title=${encodeURIComponent(film.title)}`);
	}

	function onSearchBlur() {
		setTimeout(() => {
			searchOpen = false;
		}, 150);
	}

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
				<div class="position-relative search-wrapper">
					<input
						type="search"
						class="form-control form-control-sm search-input"
						placeholder="Search films…"
						bind:value={searchQuery}
						oninput={onSearchInput}
						onblur={onSearchBlur}
						autocomplete="off"
					/>
					{#if searchOpen}
						<ul class="search-dropdown list-unstyled mb-0">
							{#each searchResults as film}
								<li>
									<button
										type="button"
										class="search-dropdown-item w-100 text-start"
										onmousedown={() => selectFilm(film)}
									>
										{film.title}
									</button>
								</li>
							{/each}
						</ul>
					{/if}
				</div>

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
								<option value={store.storeId} selected={store.storeId === selectedStoreId}
									>Store #{store.storeId}
									– {store.address}</option
								>
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
		background-clip: text;
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

	.search-wrapper {
		width: 18rem;
	}

	.search-input {
		background-color: #495057;
		color: #fff;
		border: none;
	}

	.search-input::placeholder {
		color: rgba(255, 255, 255, 0.5);
	}

	.search-input:focus {
		background-color: #495057;
		color: #fff;
		box-shadow: 0 0 0 0.25rem rgba(13, 202, 240, 0.25);
	}

	.search-dropdown {
		position: absolute;
		top: calc(100% + 4px);
		left: 0;
		right: 0;
		background: #2b2d31;
		border: 1px solid #444;
		border-radius: 0.375rem;
		max-height: 16rem;
		overflow-y: auto;
		z-index: 1050;
		box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
	}

	.search-dropdown-item {
		display: block;
		padding: 0.5rem 0.75rem;
		color: #e0e0e0;
		background: transparent;
		border: none;
		font-size: 0.875rem;
		cursor: pointer;
	}

	.search-dropdown-item:hover {
		background-color: #0d6efd;
		color: #fff;
	}
</style>
