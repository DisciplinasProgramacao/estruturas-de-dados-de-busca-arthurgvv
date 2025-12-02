import java.nio.charset.Charset;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Quantidade de produtos cadastrados atualmente na lista */
    static int quantosProdutos = 0;

    static ABB<String, Produto> produtosCadastradosPorNome;
    
    static ABB<Integer, Produto> produtosCadastradosPorId;
    
    static AVL<String, Produto> produtosBalanceadosPorNome;
    
    static AVL<Integer, Produto> produtosBalanceadosPorId;
    
    // Variáveis para medir tempo de construção
    static long tempoConstABBNome;
    static long tempoConstABBId;
    static long tempoConstAVLNome;
    static long tempoConstAVLId;
    
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
   
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * Perceba que poderia haver uma melhor modularização com a criação de uma classe Menu.
     * @return Um inteiro com a opção do usuário.
    */
    static int menu() {
        cabecalho();
        System.out.println("1 - Carregar produtos por nome/descrição (ABB)");
        System.out.println("2 - Carregar produtos por id (ABB)");
        System.out.println("3 - Carregar produtos por nome/descrição (AVL)");
        System.out.println("4 - Carregar produtos por id (AVL)");
        System.out.println("5 - Procurar produto, por nome (ABB)");
        System.out.println("6 - Procurar produto, por id (ABB)");
        System.out.println("7 - Procurar produto, por nome (AVL)");
        System.out.println("8 - Procurar produto, por id (AVL)");
        System.out.println("9 - Comparar desempenho: busca por ID (ABB vs AVL)");
        System.out.println("10 - Remover produto, por nome");
        System.out.println("11 - Remover produto, por id");
        System.out.println("12 - Recortar a lista de produtos, por nome");
        System.out.println("13 - Recortar a lista de produtos, por id");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna uma árvore ABB de produtos. Arquivo-texto no formato
     * N (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna uma árvore vazia em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Uma árvore ABB com os produtos carregados, ou vazia em caso de problemas de leitura.
     */
    static <K> ABB<K, Produto> lerProdutosABB(String nomeArquivoDados, Function<Produto, K> extratorDeChave) {
    	
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	ABB<K, Produto> produtosCadastrados;
    	K chave;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new ABB<K, Produto>();
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			chave = extratorDeChave.apply(produto);
    			produtosCadastrados.inserir(chave, produto);
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return produtosCadastrados;
    }
    
    /**
     * Lê os dados de um arquivo-texto e retorna uma árvore AVL de produtos. Arquivo-texto no formato
     * N (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna uma árvore vazia em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Uma árvore AVL com os produtos carregados, ou vazia em caso de problemas de leitura.
     */
    static <K> AVL<K, Produto> lerProdutosAVL(String nomeArquivoDados, Function<Produto, K> extratorDeChave) {
    	
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	AVL<K, Produto> produtosCadastrados;
    	K chave;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		produtosCadastrados = new AVL<K, Produto>();
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			chave = extratorDeChave.apply(produto);
    			produtosCadastrados.inserir(chave, produto);
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		produtosCadastrados = null;
    	} finally {
    		arquivo.close();
    	}
    	
    	return produtosCadastrados;
    }
    
    static <K> Produto localizarProduto(ABB<K, Produto> produtosCadastrados, K procurado) {
    	
    	Produto produto;
    	
    	cabecalho();
    	System.out.println("Localizando um produto...");
    	
    	try {
    		produto = produtosCadastrados.pesquisar(procurado);
    	} catch (NoSuchElementException excecao) {
    		produto = null;
    	}
    	
    	System.out.println("Número de comparações realizadas: " + produtosCadastrados.getComparacoes());
    	System.out.println("Tempo de processamento da pesquisa: " + produtosCadastrados.getTempo() + " ms");
        
    	return produto;
    	
    }
    
    /** Localiza um produto na árvore de produtos organizados por id, a partir do código de produto informado pelo usuário, e o retorna. 
     *  Em caso de não encontrar o produto, retorna null */
    static Produto localizarProdutoID(ABB<Integer, Produto> produtosCadastrados) {
        
        int idProduto = lerOpcao("Digite o identificador do produto desejado: ", Integer.class);
        
        return localizarProduto(produtosCadastrados, idProduto);
    }
    
    /** Localiza um produto na árvore de produtos organizados por nome, a partir do nome de produto informado pelo usuário, e o retorna. 
     *  A busca não é sensível ao caso. Em caso de não encontrar o produto, retorna null */
    static Produto localizarProdutoNome(ABB<String, Produto> produtosCadastrados) {
        
    	String descricao;
    	
    	System.out.println("Digite o nome ou a descrição do produto desejado:");
        descricao = teclado.nextLine();
        
        return localizarProduto(produtosCadastrados, descricao);
    }
    
    private static void mostrarProduto(Produto produto) {
    	
        cabecalho();
        String mensagem = "Dados inválidos para o produto!";
        
        if (produto != null){
            mensagem = String.format("Dados do produto:\n%s", produto);
        }
        
        System.out.println(mensagem);
    }
    
    /** Localiza e remove um produto da árvore de produtos organizados por id, a partir do código de produto informado pelo usuário, e o retorna. 
     *  Em caso de não encontrar o produto, retorna null */
    static Produto removerProdutoId(ABB<Integer, Produto> produtosCadastrados) {
         cabecalho();
         System.out.println("Localizando o produto por id");
         int id = lerOpcao("Digite o id do produto que deve ser removido", Integer.class);
         Produto localizado =  removerProduto(produtosCadastrados, id);
         return localizado;
    }

     /** Localiza e remove um produto na árvore de produtos organizados por nome, a partir do nome de produto informado pelo usuário, e o retorna. 
      *  A busca não é sensível ao caso. Em caso de não encontrar o produto, retorna null */
    static Produto removerProdutoNome(ABB<String, Produto> produtosCadastrados) {
    	String descricao;
         
    	cabecalho();
        System.out.println("Localizando o produto por nome");
        System.out.print("Digite a descrição do produto que deve ser removido: ");
        descricao = teclado.nextLine();
        Produto localizado =  removerProduto(produtosCadastrados, descricao);
        return localizado;
    }

    static <K> Produto removerProduto(ABB<K, Produto> produtosCadastrados, K chave){
         cabecalho();
         Produto localizado =  produtosCadastrados.remover(chave);
         return localizado;
    }
    
    private static void recortarProdutosNome(ABB<String, Produto> produtosCadastrados) {
     	
        if (produtosCadastrados == null || produtosCadastrados.vazia()) {
        	cabecalho();
        	System.out.println("Erro: Carregue os dados primeiro!");
        	return;
        }
        
        cabecalho();
        System.out.println("RECORTAR PRODUTOS POR NOME");
        System.out.println("==========================");
        
        System.out.print("Digite o nome inicial (de onde): ");
        String nomeInicial = teclado.nextLine();
        
        System.out.print("Digite o nome final (até onde): ");
        String nomeFinal = teclado.nextLine();
        
        try {
            Lista<Produto> resultado = produtosCadastrados.recortar(nomeInicial, nomeFinal);
            
            System.out.println("\nProdutos encontrados no intervalo:");
            System.out.println(resultado);
        } catch (Exception e) {
            System.out.println("Erro ao recortar: " + e.getMessage());
        }
     }
     
    private static void recortarProdutosId(ABB<Integer, Produto> produtosCadastrados) {
     	
        if (produtosCadastrados == null || produtosCadastrados.vazia()) {
        	cabecalho();
        	System.out.println("Erro: Carregue os dados primeiro!");
        	return;
        }
        
        cabecalho();
        System.out.println("RECORTAR PRODUTOS POR ID");
        System.out.println("========================");
        
        Integer idInicial = lerOpcao("Digite o ID inicial (de onde): ", Integer.class);
        Integer idFinal = lerOpcao("Digite o ID final (até onde): ", Integer.class);
        
        if (idInicial == null || idFinal == null) {
            System.out.println("Erro: IDs inválidos!");
            return;
        }
        
        try {
            Lista<Produto> resultado = produtosCadastrados.recortar(idInicial, idFinal);
            
            System.out.println("\nProdutos encontrados no intervalo:");
            System.out.println(resultado);
        } catch (Exception e) {
            System.out.println("Erro ao recortar: " + e.getMessage());
        }
    }
    
    /**
     * Compara o desempenho de busca por ID entre as árvores ABB e AVL.
     * Realiza múltiplas buscas e apresenta estatísticas comparativas.
     */
    private static void compararDesempenhoBuscaId() {
    	
    	if (produtosCadastradosPorId == null || produtosBalanceadosPorId == null) {
    		cabecalho();
    		System.out.println("Erro: Carregue os dados em ambas as árvores (ABB e AVL) primeiro!");
    		return;
    	}
    	
    	cabecalho();
    	System.out.println("COMPARAÇÃO DE DESEMPENHO: BUSCA POR ID");
    	System.out.println("=====================================");
    	System.out.println("Árvore ABB carregada em: " + tempoConstABBId + " ms");
    	System.out.println("Árvore AVL carregada em: " + tempoConstAVLId + " ms");
    	System.out.println();
    	
    	// Realiza múltiplas buscas para comparação
    	int numBuscas = 10;
    	long totalComparacoesABB = 0;
    	double totalTempoABB = 0;
    	long totalComparacoesAVL = 0;
    	double totalTempoAVL = 0;
    	
    	System.out.println("Realizando " + numBuscas + " buscas aleatórias em ambas as árvores...");
    	System.out.println();
    	
    	for (int i = 1; i <= numBuscas; i++) {
    		int idAleatorio = (int) (Math.random() * quantosProdutos) + 1;
    		
    		try {
    			// Busca na ABB
    			produtosCadastradosPorId.pesquisar(idAleatorio);
    			totalComparacoesABB += produtosCadastradosPorId.getComparacoes();
    			totalTempoABB += produtosCadastradosPorId.getTempo();
    			
    			// Busca na AVL
    			produtosBalanceadosPorId.pesquisar(idAleatorio);
    			totalComparacoesAVL += produtosBalanceadosPorId.getComparacoes();
    			totalTempoAVL += produtosBalanceadosPorId.getTempo();
    			
    		} catch (NoSuchElementException e) {
    			// Ignora produtos não encontrados
    		}
    	}
    	
    	System.out.println("RESULTADOS DA COMPARAÇÃO:");
    	System.out.println("========================");
    	System.out.println("ABB:");
    	System.out.println("  - Comparações médias: " + (totalComparacoesABB / numBuscas));
    	System.out.println("  - Tempo médio: " + String.format("%.6f", totalTempoABB / numBuscas) + " ms");
    	System.out.println();
    	System.out.println("AVL:");
    	System.out.println("  - Comparações médias: " + (totalComparacoesAVL / numBuscas));
    	System.out.println("  - Tempo médio: " + String.format("%.6f", totalTempoAVL / numBuscas) + " ms");
    	System.out.println();
    	
    	double melhoria = ((totalTempoABB - totalTempoAVL) / totalTempoABB) * 100;
    	if (melhoria > 0) {
    		System.out.println("A AVL foi " + String.format("%.2f", melhoria) + "% mais rápida que a ABB");
    	} else {
    		System.out.println("A ABB foi " + String.format("%.2f", -melhoria) + "% mais rápida que a AVL");
    	}
    }
    
	public static void main(String[] args) {
		teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        nomeArquivoDados = "produtos.txt";
        
        int opcao = -1;
      
        do{
            opcao = menu();
            switch (opcao) {
            	case 1 -> {
            		long inicio = System.currentTimeMillis();
            		produtosCadastradosPorNome = lerProdutosABB(nomeArquivoDados, (p -> p.descricao));
            		tempoConstABBNome = System.currentTimeMillis() - inicio;
            		cabecalho();
            		System.out.println("Produtos carregados em ABB por nome!");
            		System.out.println("Tempo de construção: " + tempoConstABBNome + " ms");
            	}
            	case 2 -> {
            		long inicio = System.currentTimeMillis();
            		produtosCadastradosPorId = lerProdutosABB(nomeArquivoDados, (p -> p.idProduto));
            		tempoConstABBId = System.currentTimeMillis() - inicio;
            		cabecalho();
            		System.out.println("Produtos carregados em ABB por id!");
            		System.out.println("Tempo de construção: " + tempoConstABBId + " ms");
            	}
            	case 3 -> {
            		long inicio = System.currentTimeMillis();
            		produtosBalanceadosPorNome = lerProdutosAVL(nomeArquivoDados, (p -> p.descricao));
            		tempoConstAVLNome = System.currentTimeMillis() - inicio;
            		cabecalho();
            		System.out.println("Produtos carregados em AVL por nome!");
            		System.out.println("Tempo de construção: " + tempoConstAVLNome + " ms");
            	}
            	case 4 -> {
            		long inicio = System.currentTimeMillis();
            		produtosBalanceadosPorId = lerProdutosAVL(nomeArquivoDados, (p -> p.idProduto));
            		tempoConstAVLId = System.currentTimeMillis() - inicio;
            		cabecalho();
            		System.out.println("Produtos carregados em AVL por id!");
            		System.out.println("Tempo de construção: " + tempoConstAVLId + " ms");
            	}
            	case 5 -> mostrarProduto(localizarProdutoNome(produtosCadastradosPorNome));
            	case 6 -> mostrarProduto(localizarProdutoID(produtosCadastradosPorId));
            	case 7 -> mostrarProduto(localizarProdutoNome((ABB<String, Produto>) produtosBalanceadosPorNome));
            	case 8 -> mostrarProduto(localizarProdutoID((ABB<Integer, Produto>) produtosBalanceadosPorId));
            	case 9 -> compararDesempenhoBuscaId();
            	case 10 -> mostrarProduto(removerProdutoNome(produtosCadastradosPorNome)); 
            	case 11 -> mostrarProduto(removerProdutoId(produtosCadastradosPorId));
            	case 12 -> recortarProdutosNome(produtosCadastradosPorNome); 
            	case 13 -> recortarProdutosId(produtosCadastradosPorId); 
            }
            if (opcao != 0)
            	pausa();
        }while(opcao != 0);       

        teclado.close();    
    }
}
