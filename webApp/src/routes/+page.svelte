<script lang="ts">
	import { getContext } from 'svelte';
	import type { User } from 'shared';

	let user = getContext<User>('user');
</script>

<p>Welcome {user.firstName}</p>
