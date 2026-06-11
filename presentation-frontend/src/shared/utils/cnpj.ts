export function formatCnpj(value: string): string {
	const digits = value.replace(/\D/g, '').slice(0, 14);
	return digits
		.replace(/^(\d{2})(\d)/, '$1.$2')
		.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
		.replace(/\.(\d{3})(\d)/, '.$1/$2')
		.replace(/(\d{4})(\d)/, '$1-$2');
}

export function normalizeCnpj(value: string): string {
	return value.replace(/\D/g, '');
}

export function isCnpjValid(value: string): boolean {
	const digits = normalizeCnpj(value);
	if (digits.length !== 14 || /^(\d)\1+$/.test(digits)) return false;

	const calc = (length: number) => {
		let sum = 0;
		let pos = length - 7;
		for (let i = length; i >= 1; i--) {
			sum += Number(digits.charAt(length - i)) * pos--;
			if (pos < 2) pos = 9;
		}
		const result = sum % 11 < 2 ? 0 : 11 - (sum % 11);
		return result;
	};

	const d1 = calc(12);
	const d2 = calc(13);
	return d1 === Number(digits.charAt(12)) && d2 === Number(digits.charAt(13));
}
