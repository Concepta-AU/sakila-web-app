<script lang="ts">
	import favicon from '$lib/assets/favicon.svg';
	import { onMount, setContext } from 'svelte';
	import { goto } from '$app/navigation';
	import type { User } from 'shared';

	let { children } = $props();
	let user = $state<User | null>(null);

	setContext('user', {
		get: () => user,
		set: (newUser: any) => (user = newUser)
	});

	onMount(() => {
		if (!user) {
			goto('/login');
		}
	});
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
</svelte:head>

<h3>Sakila Store App</h3>
<div class="content">
	{@render children()}
</div>

<style>
	.content {
		padding: 1rem;
	}
</style>
