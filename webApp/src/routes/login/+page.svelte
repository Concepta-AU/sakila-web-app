<script lang="ts">
	import { getContext } from 'svelte';
	import { goto } from '$app/navigation';

	let username = $state('');
	let password = $state('');
	let error = $state('');

	let user = getContext<any>('user');

	async function login() {
		const response = await fetch('/api/login', {
			method: 'POST',
			body: JSON.stringify({ username, password }),
			headers: { 'Content-Type': 'application/json' }
		});
		if (response.status !== 200) {
			error = await response.text();
		} else {
			const result = await response.json();
			user.set(result);
			await goto('/');
		}
	}
</script>

<h5>Login</h5>

<div class="login-form">
	<form>
		<div class="mb-3">
			<label for="usernameInput" class="form-label">Username</label>
			<input id="usernameInput" type="username" class="form-control" bind:value={username} />
		</div>
		<div class="mb-3">
			<label for="passwordInput" class="form-label">Password</label>
			<input id="passwordInput1" type="password" class="form-control" bind:value={password} />
		</div>
		<button onclick={login} class="btn btn-primary">Login</button>
	</form>
</div>

{#if error}
	<div class="error">
		{error}
	</div>
{/if}

<style>
	.login-form {
		width: 20rem;
		padding-inline-start: 1rem;
	}

	.error {
		background: darkred;
		color: whitesmoke;
		margin: 1rem;
		padding: 1rem;
	}
</style>
