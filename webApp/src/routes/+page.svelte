<script lang="ts">
	import Textfield from '@smui/textfield';
	import Button, { Label } from '@smui/button';

	let username = $state('');
	let password = $state('');
	let error = $state('');

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
			console.log(result);
		}
	}
</script>

<h2>Login</h2>
<div class="columns margins">
	<div>
		<Textfield bind:value={username} label="Username" />
	</div>
	<div>
		<Textfield bind:value={password} label="Password" type="password" />
	</div>
	{#if error}
		<div class="error">
			{error}
		</div>
	{/if}
	<Button onclick={login}>
		<Label>Log in</Label>
	</Button>
</div>

<style>
    .error {
        background: darkred;
        color: whitesmoke;
        margin: 1rem;
        padding: 1rem;
    }
</style>