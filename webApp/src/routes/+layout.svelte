<script lang="ts">
	import favicon from '$lib/assets/favicon.svg';
	import { onMount, setContext } from 'svelte';
	import { goto } from '$app/navigation';

	let { children } = $props();
	let user = $state<any>({});

	setContext('user', {
		get: () => user,
		set: (newUser: any) => user = newUser
	});

	onMount(() => {
		if (!user.token) {
			goto('/login');
		}
	});
</script>

<svelte:head>
	<link rel="icon" href={favicon} />
</svelte:head>

<h3>Sakila Store App</h3>
{@render children()}
