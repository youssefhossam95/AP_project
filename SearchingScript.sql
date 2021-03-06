USE [master]
GO
/****** Object:  Database [SearchEngine]    Script Date: 5/20/2017 10:39:25 PM ******/
CREATE DATABASE [SearchEngine]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'SearchEngine', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL12.SQLEXPRESS\MSSQL\DATA\SearchEngine.mdf' , SIZE = 797696KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON 
( NAME = N'SearchEngine_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL12.SQLEXPRESS\MSSQL\DATA\SearchEngine_log.ldf' , SIZE = 353408KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
ALTER DATABASE [SearchEngine] SET COMPATIBILITY_LEVEL = 120
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [SearchEngine].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [SearchEngine] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [SearchEngine] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [SearchEngine] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [SearchEngine] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [SearchEngine] SET ARITHABORT OFF 
GO
ALTER DATABASE [SearchEngine] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [SearchEngine] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [SearchEngine] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [SearchEngine] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [SearchEngine] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [SearchEngine] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [SearchEngine] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [SearchEngine] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [SearchEngine] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [SearchEngine] SET  DISABLE_BROKER 
GO
ALTER DATABASE [SearchEngine] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [SearchEngine] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [SearchEngine] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [SearchEngine] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [SearchEngine] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [SearchEngine] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [SearchEngine] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [SearchEngine] SET RECOVERY SIMPLE 
GO
ALTER DATABASE [SearchEngine] SET  MULTI_USER 
GO
ALTER DATABASE [SearchEngine] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [SearchEngine] SET DB_CHAINING OFF 
GO
ALTER DATABASE [SearchEngine] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [SearchEngine] SET TARGET_RECOVERY_TIME = 0 SECONDS 
GO
ALTER DATABASE [SearchEngine] SET DELAYED_DURABILITY = DISABLED 
GO
USE [SearchEngine]
GO
/****** Object:  Table [dbo].[Page]    Script Date: 5/20/2017 10:39:25 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Page](
	[URL] [varchar](450) NOT NULL,
	[Body] [varchar](max) NULL,
	[Rank] [int] NULL CONSTRAINT [DF_Page_Rank]  DEFAULT ((1)),
	[Title] [varchar](max) NULL,
	[Headers] [varchar](max) NULL,
	[LastFetched] [datetime] NULL,
	[isIndexed] [bit] NULL,
	[NumberOfWords] [int] NULL,
 CONSTRAINT [PK_Page_1] PRIMARY KEY CLUSTERED 
(
	[URL] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[PointsTo]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[PointsTo](
	[Source] [varchar](450) NOT NULL,
	[Destination] [varchar](450) NOT NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[SearchWords]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[SearchWords](
	[SearchedPhrases] [varchar](255) NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[UContains]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[UContains](
	[URL] [varchar](450) NOT NULL,
	[WordID] [int] NOT NULL,
	[Priority] [tinyint] NOT NULL,
	[Index] [int] NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[Word]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Word](
	[ID] [int] NOT NULL,
	[Text] [varchar](100) NOT NULL,
	[StemmedText] [varchar](100) NULL,
	[Difference] [tinyint] NOT NULL,
 CONSTRAINT [PK_Word] PRIMARY KEY CLUSTERED 
(
	[Text] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
ALTER TABLE [dbo].[PointsTo]  WITH CHECK ADD  CONSTRAINT [FK_PointsTo_PointsTo] FOREIGN KEY([Source])
REFERENCES [dbo].[Page] ([URL])
GO
ALTER TABLE [dbo].[PointsTo] CHECK CONSTRAINT [FK_PointsTo_PointsTo]
GO
ALTER TABLE [dbo].[UContains]  WITH CHECK ADD  CONSTRAINT [FK_Contains_Page] FOREIGN KEY([URL])
REFERENCES [dbo].[Page] ([URL])
GO
ALTER TABLE [dbo].[UContains] CHECK CONSTRAINT [FK_Contains_Page]
GO
/****** Object:  StoredProcedure [dbo].[DeleteURL]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROC [dbo].[DeleteURL] 
@URL varchar (450 ) 
AS 
DELETE FROM UContains WHERE URL = @URL 




GO
/****** Object:  StoredProcedure [dbo].[findPage]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE proc [dbo].[findPage] @url varchar(450),@title varchar(Max) output
as
begin 
select @title=Title from page where URL=@url;
end 






GO
/****** Object:  StoredProcedure [dbo].[GetCountOfThisWord]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

--get the number of occ of a wordid aw url for a specific word (donc number of occ of this word in this url)
CREATE PROCEDURE [dbo].[GetCountOfThisWord] @url varchar(MAX), @wordID int,@countOfThisWord int output
AS
BEGIN
SELECT COUNT(URL)
FROM UContains u
WHERE u.URL=@url AND u.WordID=@wordID 
END



GO
/****** Object:  StoredProcedure [dbo].[GetCountOfThisWordAndStem]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


--get the number of occ of a wordid aw url for a specific word (donc number of occ of this word in this url)
CREATE PROCEDURE [dbo].[GetCountOfThisWordAndStem] @url varchar(MAX), @stem varchar(50), @countOfThisWord int output
AS
BEGIN
SELECT COUNT(URL)
FROM UContains u, Word w
WHERE u.WordID=w.ID AND u.URL=@url AND w.StemmedText=@stem
END



GO
/****** Object:  StoredProcedure [dbo].[GetCountOfWords]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

----get the number of pages that this word occ in
--CREATE PROCEDURE GetNumPages @WordID int, @NumberOfPages int output
--AS
--BEGIN
--SELECT COUNT(URL) AS NumberOfPagesForThisWord
--FROM UContains U
--WHERE U.WordID=@WordID  
--END
--GO

--DECLARE @wordID int=1
--DECLARE @NumberOfPages int
--EXEC GetNumPages @wordID, @NumberOfPages output

--GO


--get the number of occ of certain url ( so the number of words in this url)
CREATE PROCEDURE [dbo].[GetCountOfWords] @url varchar(100), @countOfWords int output
AS
BEGIN
SELECT COUNT(URL)
FROM UContains u
WHERE u.URL=@url 
END



GO
/****** Object:  StoredProcedure [dbo].[GetFetchedPages]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

--get number of fetched pages
CREATE PROCEDURE [dbo].[GetFetchedPages] @NumFetchedPages int output
AS 
BEGIN 
SELECT COUNT(URL)
FROM Page
END



GO
/****** Object:  StoredProcedure [dbo].[GetID1]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


--Get the wordID for a specific word without stemming
CREATE PROCEDURE [dbo].[GetID1] @Word varchar(50), @wordIDD int output
AS
BEGIN
SELECT DISTINCT WordID AS WORDID
FROM UContains U ,Word W
WHERE U.WordID=W.ID AND W.Text=@Word
END



GO
/****** Object:  StoredProcedure [dbo].[getPageAge]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE proc [dbo].[getPageAge] @url varchar(450), @result int output
as
begin
declare @currenthour int =DATEPART(hh,getDate()) 
declare @oldhour int= DATEPART(hh,(select lastfetched from page where url=@url)); 
declare @hourdiff int = @currenthour-@oldhour
declare @currentday int =DATEPART(dd,getDate()) 
declare @oldday int= DATEPART(dd,(select lastfetched from page where url=@url)); 
declare @daydiff int = @currentday-@oldday
declare @currentmonth int=datepart(mm,getDate())
declare @oldmonth int=datepart(mm,(select lastfetched from page where url=@url))
declare @monthdiff int= @currentmonth - @oldmonth 
if @daydiff!=0 or @monthdiff!=0
set @result=-1;
else
set @result=@hourdiff;
end 






GO
/****** Object:  StoredProcedure [dbo].[GetPageTitle]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROCEDURE  [dbo].[GetPageTitle] 
@URL varchar (450 ) 
AS 
 select Title from Page where page.URL= @URL 





GO
/****** Object:  StoredProcedure [dbo].[GetPopularity]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

--get the popularity of url
CREATE procedure [dbo].[GetPopularity] @URL varchar(max), @Popularity int output
as
begin
select distinct Rank 
from Page P
where P.URL=@URL
end



GO
/****** Object:  StoredProcedure [dbo].[GetPriority]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


--get priority
CREATE PROCEDURE [dbo].[GetPriority] @wordID int, @Priority int output
AS
BEGIN
SELECT Priority
FROM UContains U
WHERE U.WordID=@wordID
END



GO
/****** Object:  StoredProcedure [dbo].[GetPriorityAndDiff]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


--to select diff and priority
CREATE procedure [dbo].[GetPriorityAndDiff] @stem varchar(50)
as
begin
select WordID as ID, Priority as Priorityy, Difference as Difference  , P.URL  , P.NumberOfWords 
from UContains u, Word w , Page P 
where u.WordID=w.ID and u.URL= p.URL and  w.StemmedText=@stem
end 



GO
/****** Object:  StoredProcedure [dbo].[GetSpecificWord]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create proc [dbo].[GetSpecificWord] 
@word varchar (50) ,
@indo int , 
@url varchar (100) 
as 

select URL , [Index] from UContains U , Word W where U.WordID = W.ID and W.Text = @word 
and U.[Index] = @indo and U.URL = @url; 


GO
/****** Object:  StoredProcedure [dbo].[GetStemmedID1]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

--get id for stemmed

CREATE PROCEDURE [dbo].[GetStemmedID1] @Word varchar(50)
AS
BEGIN
SELECT ID AS StemID
FROM Word W
WHERE W.StemmedText=@Word
END



GO
/****** Object:  StoredProcedure [dbo].[GetStemmedWords]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

--get all words for this stemmed word
CREATE PROCEDURE [dbo].[GetStemmedWords] @Word varchar(50)
AS
BEGIN
SELECT Text AS OrgWordsForThisStem
FROM Word W
WHERE W.StemmedText=@Word
END



GO
/****** Object:  StoredProcedure [dbo].[GetStemWord]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

--Get the stemmed word for a specific word id
CREATE PROCEDURE [dbo].[GetStemWord] @WID int, @stem varchar(50) output
AS
BEGIN
SELECT DISTINCT StemmedText
FROM Word W
WHERE W.ID=@WID
END



GO
/****** Object:  StoredProcedure [dbo].[GetURL]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


--Get the url for a specific wordID 
CREATE PROCEDURE [dbo].[GetURL] @WordID int
AS
BEGIN
SELECT DISTINCT URL
FROM UContains U, Word W
WHERE U.WordID=W.ID AND W.ID=@WordID  
END



GO
/****** Object:  StoredProcedure [dbo].[GetWord]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROC [dbo].[GetWord] 
@Word varchar (100 ) 
AS 
SELECT ID FROM Word WHERE Text = @Word ; 







GO
/****** Object:  StoredProcedure [dbo].[GetWordIndex]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create proc [dbo].[GetWordIndex] 
@word varchar (50) 

as 

select URL , [Index] from UContains U , Word W where U.WordID = W.ID and W.Text = @word ; 

GO
/****** Object:  StoredProcedure [dbo].[incrementRank]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE proc [dbo].[incrementRank] @url varchar(450)
as
begin
update page set rank=(select rank from page where URL=@url)+1 where URL=@url;
end





GO
/****** Object:  StoredProcedure [dbo].[insertPage]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create proc [dbo].[insertPage] @url varchar(450),@body varchar(MAX),@title varchar(MAX),@header varchar(Max)
as 
begin 
insert into page (URL,Body,Title,Headers,LastFetched) values(@url,@body,@title,@header,GETDATE())
end







GO
/****** Object:  StoredProcedure [dbo].[insertPointsTo]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
create proc [dbo].[insertPointsTo] @src varchar(450),@dest varchar(450)
as 
begin
insert into PointsTo values(@src,@dest);
end







GO
/****** Object:  StoredProcedure [dbo].[InsertUContains]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROC [dbo].[InsertUContains]  
@URL varchar(450), 
@ID int ,
@Priority tinyint, 
@Index int 
AS 
INSERT INTO UContains VALUES (@URL ,@ID ,@Priority,@Index ) ; 





GO
/****** Object:  StoredProcedure [dbo].[InsertWord]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE PROC [dbo].[InsertWord] 
@ID int ,
@Word varchar (100) , 
@StemmedWord varchar (100 ) ,
@Difference tinyint 
AS 
INSERT INTO Word VALUES ( @ID ,@Word, @StemmedWord, @Difference ) ; 






GO
/****** Object:  StoredProcedure [dbo].[isoldPage]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE proc [dbo].[isoldPage] @url varchar(450), @result int output
as
begin
declare @currenthour int =DATEPART(hh,getDate()) 
declare @oldhour int= DATEPART(hh,(select lastfetched from page where url=@url)); 
declare @hourdiff int = @currenthour-@oldhour
declare @currentday int =DATEPART(dd,getDate()) 
declare @oldday int= DATEPART(dd,(select lastfetched from page where url=@url)); 
declare @daydiff int = @currentday-@oldday
if (@daydiff!=0) or (@hourdiff>1) 
set @result=1;
else 
set @result=0;
end 






GO
/****** Object:  StoredProcedure [dbo].[MarkURLIndexed]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROC [dbo].[MarkURLIndexed]  
@URL varchar (450 ) 
AS 
UPDATE Page
SET isIndexed = 1 
WHERE URL = @URL




GO
/****** Object:  StoredProcedure [dbo].[numOfPagesOfWord]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE proc [dbo].[numOfPagesOfWord] 
@stemmedText varchar (50)
as 
SELECT COUNT ( DISTINCT URL ) as count 
FROM UContains U, Word W
WHERE U.WordID=W.ID AND W.StemmedText= @stemmedText

GO
/****** Object:  StoredProcedure [dbo].[tryInsertPage]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE proc [dbo].[tryInsertPage] @url varchar(450)
as
begin
insert into page(URL) values(@url);
end






GO
/****** Object:  StoredProcedure [dbo].[UpdateNumberOfWords]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

create proc [dbo].[UpdateNumberOfWords] 
@Num int , 
@url varchar (100) 

as 
Update  Page set NumberOfWords = @Num where URL = @url  

GO
/****** Object:  StoredProcedure [dbo].[updatePage]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE proc [dbo].[updatePage] @text varchar(Max),@title varchar(Max),@header varchar(Max),@Url varchar(450)
as 
begin
update Page set Page.Body=@text,Page.Headers=@header,Page.Title=@title,Page.LastFetched=GETDATE() ,isIndexed=0 where Page.URL=@Url
end






GO
/****** Object:  StoredProcedure [dbo].[URLsOfThisStem]    Script Date: 5/20/2017 10:39:26 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO


CREATE procedure [dbo].[URLsOfThisStem] @word varchar(50)
as
begin
select distinct URL
from UContains u, Word w
where u.WordID=w.ID and w.StemmedText=@word
end 



GO
EXEC sys.sp_addextendedproperty @name=N'DURABILITY ', @value=N'SCHEMA_AND_DATA' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Page'
GO
EXEC sys.sp_addextendedproperty @name=N'MEMORY_OPTIMIZED', @value=N'ON' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Page'
GO
EXEC sys.sp_addextendedproperty @name=N'DURABILITY ', @value=N'SCHEMA_AND_DATA' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'UContains'
GO
EXEC sys.sp_addextendedproperty @name=N'MEMORY_OPTIMIZED', @value=N'ON' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'UContains'
GO
EXEC sys.sp_addextendedproperty @name=N'DURABILITY ', @value=N'SCHEMA_AND_DATA' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Word'
GO
EXEC sys.sp_addextendedproperty @name=N'MEMORY_OPTIMIZED', @value=N'ON' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'TABLE',@level1name=N'Word'
GO
USE [master]
GO
ALTER DATABASE [SearchEngine] SET  READ_WRITE 
GO
