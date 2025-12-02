import java.util.Objects;

public final class Entrada<K, V> {

	private final K chave;
	private volatile V valor;
	
	public Entrada(K chave, V valor) {
		Objects.requireNonNull(chave, "Chave não pode ser nula");
		this.chave = chave;
		this.valor = valor;
	}

	public K extrairChave() {
		return chave;
	}
	
	public V extrairValor() {
		return valor;
	}
	
	public void atualizarValor(V novoValor) {
		this.valor = novoValor;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object outro) {
		if (this == outro) return true;
		if (outro == null || !(outro instanceof Entrada)) return false;
		Entrada<K, V> that = (Entrada<K, V>) outro;
		return this.chave.equals(that.chave);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(chave);
	}
	
	@Override
	public String toString() {
		return String.format("%s -> %s", chave, valor);
	}
}
