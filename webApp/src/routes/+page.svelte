<script lang="ts">
	import { getContext } from 'svelte';

	let user = getContext<any>('user');
</script>

<p>Welcome {user.get().firstName}</p>
