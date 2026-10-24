# Catalogue de matériel informatique — Spring et Hibernate

Cette implémentation du TP conserve Spring 5, Hibernate 5, JPA, MySQL et le
DAO générique `IDao<T>`. Le domaine a été personnalisé en catalogue de matériel
informatique avec gestion du stock.

## Architecture

```text
MainApp
   -> ProductService       règles métier et transactions
      -> ProductDao        contrat des requêtes produit
         -> ProductDaoImpl requêtes Hibernate/HQL
            -> MySQL       persistance
```

- `entity` contient l'entité `Product` et l'énumération `ProductStatus`.
- `dao` contient le CRUD générique et les requêtes de persistance.
- `service` porte les validations et les opérations métier transactionnelles.
- `exception` contient des erreurs compréhensibles par le code appelant.
- `config` construit les beans Spring, Hibernate et le gestionnaire de transactions.

## Personnalisation métier

Trois colonnes ont été ajoutées à `products` :

- `sku` : référence unique, normalisée en majuscules ;
- `stock_quantity` : quantité disponible, toujours positive ou nulle ;
- `status` : `AVAILABLE`, `OUT_OF_STOCK` ou `DISCONTINUED`.

Fonctionnalités ajoutées :

1. Entrées et sorties de stock avec refus d'une quantité impossible.
2. Statut disponible/épuisé synchronisé automatiquement avec la quantité.
3. Retrait définitif d'un produit du catalogue.
4. Recherche par nom/statut et calcul de la valeur totale du stock actif.

## Préparer MySQL

Créer la base :

```sql
CREATE DATABASE product_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

La connexion locale est directement configurée dans
`src/main/resources/application.properties` avec l'utilisateur `root` et le
mot de passe `root123`. Cette configuration est volontairement simplifiée pour
le TP et ne doit pas être reprise dans une application réelle.

Si la table `products` de la première version contient déjà des lignes,
l'ajout de la colonne obligatoire `sku` peut échouer. Pour ce laboratoire de
développement uniquement, sauvegarder les données utiles puis supprimer cette
ancienne table afin qu'Hibernate la recrée avec le nouveau schéma.

## Exécuter

Vérifier la connexion MySQL :

```powershell
mvn exec:java "-Dexec.mainClass=com.microservice.product.TestHibernate"
```

Lancer la démonstration :

```powershell
mvn exec:java "-Dexec.mainClass=com.microservice.product.MainApp"
```

Lancer les tests sur H2, sans utiliser MySQL :

```powershell
mvn test
```

Si Maven ne peut pas écrire dans le dépôt global :

```powershell
mvn "-Dmaven.repo.local=.m2" test
```

## Publier avec Git

```powershell
git init
git add .
git commit -m "Implement product inventory with Spring and Hibernate"
git branch -M main
git remote add origin URL_DU_DEPOT
git push -u origin main
```
