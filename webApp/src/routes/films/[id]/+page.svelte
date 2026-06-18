<script lang="ts">
	import { getContext } from 'svelte';
	import { page } from '$app/stores';
	import type { Film } from 'shared';

	let userContext = getContext<any>('user');
	let user = $derived(userContext.get());

	let film = $state<Film | null>(null);
	let loading = $state(true);
	let error = $state('');

	let filmId = $derived(parseInt($page.params.id ?? '0'));
	let titleHint = $derived($page.url.searchParams.get('title') ?? '');

	$effect(() => {
		if (user && filmId) {
			loading = true;
			error = '';
			film = null;
			const query = titleHint || '';
			fetch(`/api/films/search?title=${encodeURIComponent(query)}`, {
				headers: { Authorization: `Bearer ${user.token}` }
			})
				.then((r) => r.json())
				.then((films: Film[]) => {
					film = films.find((f) => f.filmId === filmId) ?? null;
					if (!film) error = 'Film not found.';
				})
				.catch(() => {
					error = 'Failed to load film.';
				})
				.finally(() => {
					loading = false;
				});
		}
	});
</script>

<div class="container py-4">
	{#if loading}
		<div class="d-flex justify-content-center py-5">
			<div class="spinner-border text-primary" role="status">
				<span class="visually-hidden">Loading…</span>
			</div>
		</div>
	{:else if error}
		<div class="alert alert-danger">{error}</div>
	{:else if film}
		<div class="card shadow-sm border-0">
			<div class="card-body p-5">
				<h1 class="display-6 fw-bold mb-3">{film.title}</h1>
				{#if film.description}
					<p class="fs-5 text-muted">{film.description}</p>
				{:else}
					<p class="text-muted fst-italic">No description available.</p>
				{/if}
			</div>
		</div>
	{/if}
</div>
