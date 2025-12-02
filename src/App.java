import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Function;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {
    
    private enum OpcaoMenu {
        BUSCAR_ID(1), GRAVAR_RELATORIO(2), SAIR(0);
        final int codigo;
        OpcaoMenu(int codigo) { this.codigo = codigo; }
        static OpcaoMenu de(int codigo) {
            for (OpcaoMenu op : values()) if (op.codigo == codigo) return op;
            return null;
        }
    }
    
    private static String nomeArquivoDados;
    private static Scanner teclado;
    private static int quantosProdutos = 0;
    private static AVL<String, Produto> produtosBalanceadosPorNome;
    private static AVL<Integer, Produto> produtosBalanceadosPorId;
    private static TabelaHash<Produto, Lista<Pedido>> pedidosPorProduto;
    
    private static class IU {
        private static final String TITULO = "AEDs II COMÉRCIO DE COISINHAS";
        private static final String LINHA = "=============================";
        
        static void limparTela() {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
        
        static void pausa() {
            System.out.println("Digite enter para continuar...");
            teclado.nextLine();
        }
        
        static void cabecalho() {
            System.out.println(TITULO);
            System.out.println(LINHA);
        }
        
        static void mostrarProduto(Produto produto) {
            cabecalho();
            if (produto == null) {
                System.out.println("Produto não encontrado!");
            } else {
                System.out.println("Dados do produto:\n" + produto);
            }
        }
        
        static int mostrarMenu() {
            cabecalho();
            System.out.println("1 - Procurar produto, por id");
            System.out.println("2 - Gravar, em arquivo, pedidos de um produto");
            System.out.println("0 - Sair");
            System.out.print("Digite sua opção: ");
            try {
                return Integer.parseInt(teclado.nextLine());
            } catch (NumberFormatException e) {
                return -1;
            }
        }
    }
    
    private static class OperacoesProduto {
        static <K> AVL<K, Produto> carregar(String arquivo, Function<Produto, K> extrator) {
            Scanner in = null;
            try {
                in = new Scanner(new File(arquivo), Charset.forName("UTF-8"));
                int n = Integer.parseInt(in.nextLine());
                AVL<K, Produto> arvore = new AVL<>();
                for (int i = 0; i < n; i++) {
                    Produto p = Produto.criarDoTexto(in.nextLine());
                    arvore.inserir(extrator.apply(p), p);
                }
                quantosProdutos = n;
                return arvore;
            } catch (IOException e) {
                return null;
            } finally {
                if (in != null) in.close();
            }
        }
        
        static <K> Produto buscar(ABB<K, Produto> arvore, K chave) {
            if (arvore == null || arvore.vazia()) {
                System.out.println("Erro: Estrutura não carregada!");
                return null;
            }
            try {
                Produto p = arvore.pesquisar(chave);
                System.out.println("Comparações: " + arvore.getComparacoes());
                System.out.println("Tempo: " + arvore.getTempo() + " ms");
                return p;
            } catch (NoSuchElementException e) {
                System.out.println("Não encontrado!");
                return null;
            }
        }
        
        static Produto buscaPorId() {
            System.out.println("Digite o ID do produto:");
            try {
                Integer id = Integer.parseInt(teclado.nextLine());
                return buscar(produtosBalanceadosPorId, id);
            } catch (NumberFormatException e) {
                System.out.println("ID inválido!");
                return null;
            }
        }
    }
    
    private static class GeracaoPedidos {
        private static final int SEED = 42;
        static Lista<Pedido> gerar(int qtd) {
            Lista<Pedido> lista = new Lista<>();
            if (produtosBalanceadosPorId == null || produtosBalanceadosPorId.vazia()) {
                System.out.println("Erro: Produtos não carregados!");
                return lista;
            }
            Random rand = new Random(SEED);
            for (int i = 0; i < qtd; i++) {
                Pedido ped = new Pedido(LocalDate.now(), rand.nextInt(2) + 1);
                for (int j = 0; j < rand.nextInt(8) + 1; j++) {
                    try {
                        Produto prod = produtosBalanceadosPorId.pesquisar(rand.nextInt(7750) + 10_000);
                        ped.incluirProduto(prod);
                        asociarProdutoPedido(prod, ped);
                    } catch (NoSuchElementException e) { }
                }
                lista.inserirFinal(ped);
            }
            return lista;
        }
        
        private static void asociarProdutoPedido(Produto prod, Pedido ped) {
            try {
                pedidosPorProduto.pesquisar(prod).inserirFinal(ped);
            } catch (NoSuchElementException e) {
                Lista<Pedido> nova = new Lista<>();
                nova.inserirFinal(ped);
                pedidosPorProduto.inserir(prod, nova);
            }
        }
    }
    
    private static void salvarRelatorio(Produto produto) {
        if (produto == null) {
            System.out.println("Produto inválido!");
            return;
        }
        String nomeArquivo = "RelatorioProduto" + produto.hashCode() + ".txt";
        try (FileWriter fw = new FileWriter(nomeArquivo, Charset.forName("UTF-8"))) {
            fw.append("RELATÓRIO DE PEDIDOS: " + produto.descricao + "\n");
            fw.append("Data: " + LocalDate.now() + "\n");
            fw.append("==========================================\n\n");
            try {
                fw.append(pedidosPorProduto.pesquisar(produto).toString() + "\n");
            } catch (NoSuchElementException e) {
                fw.append("Nenhum pedido registrado.\n");
            }
            System.out.println("Salvo em " + nomeArquivo);
        } catch(IOException e) {
            System.out.println("Erro ao salvar arquivo: " + e.getMessage());
        }
    }
    
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

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
    
    static int menu() {
        cabecalho();
        System.out.println("1 - Procurar produto, por id");
        System.out.println("2 - Gravar, em arquivo, pedidos de um produto");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }
    
    static <K> AVL<K, Produto> lerProdutos(String nomeArquivoDados, Function<Produto, K> extratorDeChave) {
    	
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
    	
    	// Validação de estrutura carregada
    	if (produtosCadastrados == null || produtosCadastrados.vazia()) {
    		System.out.println("Erro: Nenhuma estrutura de dados foi carregada!");
    		return null;
    	}
    	
    	System.out.println("Localizando um produto...");
    	
    	try {
    		produto = produtosCadastrados.pesquisar(procurado);
    	} catch (NoSuchElementException excecao) {
    		System.out.println("Produto não encontrado!");
    		produto = null;
    	}
    	
    	if (produto != null) {
    		System.out.println("Número de comparações realizadas: " + produtosCadastrados.getComparacoes());
    		System.out.println("Tempo de processamento da pesquisa: " + produtosCadastrados.getTempo() + " ms");
    	}
        
    	return produto;
    	
    }
    
    /** Localiza um produto na árvore de produtos organizados por id, a partir do código de produto informado pelo usuário, e o retorna. 
     *  Em caso de não encontrar o produto, retorna null */
    static Produto localizarProdutoID(AVL<Integer, Produto> produtosCadastrados) {
        
        Integer idProduto = lerOpcao("Digite o identificador do produto desejado: ", Integer.class);
        
        if (idProduto == null) {
        	System.out.println("ID inválido!");
        	return null;
        }
        
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
    
    private static Lista<Pedido> gerarPedidos(int quantidade) {
        Lista<Pedido> pedidos = new Lista<>();
        Random sorteio = new Random(42);
        int quantProdutos;
        int formaDePagamento;
        
        // Validação da estrutura de dados carregada
        if (produtosBalanceadosPorId == null || produtosBalanceadosPorId.vazia()) {
        	System.out.println("Erro: Nenhum produto foi carregado!");
        	return pedidos;
        }
        
        for (int i = 0; i < quantidade; i++) {
        	formaDePagamento = sorteio.nextInt(2) + 1;
            Pedido pedido = new Pedido(LocalDate.now(), formaDePagamento);
            quantProdutos = sorteio.nextInt(8) + 1;
            for (int j = 0; j < quantProdutos; j++) {
                int id = sorteio.nextInt(7750) + 10_000;
                try {
                	Produto produto = produtosBalanceadosPorId.pesquisar(id);
                	pedido.incluirProduto(produto);
                	inserirNaTabela(produto, pedido);
                } catch (NoSuchElementException e) {
                	// Produto não encontrado, continua para o próximo
                }
            }
            pedidos.inserirFinal(pedido);
        }
        return pedidos;
    }
    
    private static void inserirNaTabela(Produto produto, Pedido pedido) {
        
    	Lista<Pedido> pedidosDoProduto;
    	
    	try {
    		pedidosDoProduto = pedidosPorProduto.pesquisar(produto);
    	} catch (NoSuchElementException excecao) {
    		pedidosDoProduto = new Lista<>();
    		pedidosPorProduto.inserir(produto, pedidosDoProduto);
    	}
    	pedidosDoProduto.inserirFinal(pedido);
    }
    
    static void pedidosDoProduto() {
    	
    	Lista<Pedido> pedidosDoProduto;
    	Produto produto = localizarProdutoID(produtosBalanceadosPorId);
    	
    	// Validação do produto localizado
    	if (produto == null) {
    		System.out.println("Não é possível gerar relatório de um produto não encontrado.");
    		return;
    	}
    	
    	String nomeArquivo = "RelatorioProduto" + produto.hashCode() + ".txt";  
    	
        try {
        	FileWriter arquivoRelatorio = new FileWriter(nomeArquivo, Charset.forName("UTF-8"));
    		
        	try {
        		pedidosDoProduto = pedidosPorProduto.pesquisar(produto);
        		arquivoRelatorio.append("RELATÓRIO DE PEDIDOS DO PRODUTO: " + produto.descricao + "\n");
        		arquivoRelatorio.append("Data do relatório: " + LocalDate.now() + "\n");
        		arquivoRelatorio.append("==========================================\n\n");
        		arquivoRelatorio.append(pedidosDoProduto.toString() + "\n");
        		System.out.println("Dados salvos com sucesso em " + nomeArquivo);
        	} catch (NoSuchElementException e) {
        		System.out.println("Este produto não possui pedidos registrados.");
        		arquivoRelatorio.append("RELATÓRIO VAZIO: Este produto não possui pedidos.\n");
        	}
            
            arquivoRelatorio.close();
        } catch(IOException excecao) {
            System.out.println("Erro ao criar o arquivo " + nomeArquivo + ". Verifique as permissões e tente novamente.");        	
        }
    }
    
	public static void main(String[] args) {
		teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        nomeArquivoDados = "produtos.txt";
        
        try {
        	produtosBalanceadosPorId = lerProdutos(nomeArquivoDados, Produto::hashCode);
        	
        	// Validação do carregamento
        	if (produtosBalanceadosPorId == null || produtosBalanceadosPorId.vazia()) {
        		System.out.println("Erro crítico: Nenhum produto foi carregado do arquivo!");
        		teclado.close();
        		return;
        	}
        	
        	// Tarefa 1: Carregar árvore de produtos por nome a partir da árvore por ID
        	produtosBalanceadosPorNome = new AVL<>(produtosBalanceadosPorId, produto -> produto.descricao, String::compareTo);
        	
        	pedidosPorProduto = new TabelaHash<>((int)(quantosProdutos * 1.25));
        	
        	gerarPedidos(25_000);
        	
        } catch (Exception e) {
        	System.out.println("Erro durante a inicialização da aplicação: " + e.getMessage());
        	teclado.close();
        	return;
        }
       
        int opcao = -1;
      
        do {
            try {
                opcao = menu();
                switch (opcao) {
                	case 1 -> mostrarProduto(localizarProdutoID(produtosBalanceadosPorId));
                	case 2 -> pedidosDoProduto(); 
                	case 0 -> System.out.println("Aplicação finalizada.");
                	default -> System.out.println("Opção inválida! Digite uma opção entre 0 e 2.");
                }
                if (opcao != 0)
                	pausa();
            } catch (NumberFormatException e) {
            	System.out.println("Entrada inválida! Por favor, digite um número inteiro.");
            	pausa();
            } catch (Exception e) {
            	System.out.println("Erro inesperado: " + e.getMessage());
            	pausa();
            }
        } while(opcao != 0);       

        teclado.close();    
    }
}