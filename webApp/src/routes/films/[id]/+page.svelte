<script lang="ts">
	import { getContext } from 'svelte';
	import { page } from '$app/stores';
	import type { Actor, Film } from 'shared';

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
				<hr />
				<dl class="row mt-3">
					{#if film.releaseYear != null}
						<dt class="col-sm-4">Release Year</dt>
						<dd class="col-sm-8">{film.releaseYear}</dd>
					{/if}
					{#if film.rating != null}
						<dt class="col-sm-4">Rating</dt>
						<dd class="col-sm-8">{film.rating}</dd>
					{/if}
					{#if film.length != null}
						<dt class="col-sm-4">Length</dt>
						<dd class="col-sm-8">{film.length} min</dd>
					{/if}
					<dt class="col-sm-4">Rental Duration</dt>
					<dd class="col-sm-8">{film.rentalDuration} days</dd>
					<dt class="col-sm-4">Rental Rate</dt>
					<dd class="col-sm-8">${film.rentalRate.toFixed(2)}</dd>
					<dt class="col-sm-4">Replacement Cost</dt>
					<dd class="col-sm-8">${film.replacementCost.toFixed(2)}</dd>
					{#if film.specialFeatures != null && (film.specialFeatures as unknown as string[]).length > 0}
						<dt class="col-sm-4">Special Features</dt>
						<dd class="col-sm-8">{(film.specialFeatures as unknown as string[]).join(', ')}</dd>
					{/if}
					{#if (film.actors as unknown as Actor[]).length > 0}
						<dt class="col-sm-4">Actors</dt>
						<dd class="col-sm-8">
							{(film.actors as unknown as Actor[])
								.map((a) => `${a.firstName} ${a.lastName}`)
								.join(', ')}
						</dd>
					{/if}
				</dl>
			</div>
		</div>
	{/if}
</div>
