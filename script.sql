USE [master]
GO
/****** Object:  Database [SearchEngine]    Script Date: 28/03/2017 14:12:38 ******/
CREATE DATABASE [SearchEngine]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'SearchEngine', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL12.MSSQLSERVER\MSSQL\DATA\SearchEngine.mdf' , SIZE = 797696KB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024KB )
 LOG ON 
( NAME = N'SearchEngine_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL12.MSSQLSERVER\MSSQL\DATA\SearchEngine_log.ldf' , SIZE = 353408KB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
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
/****** Object:  Table [dbo].[Page]    Script Date: 28/03/2017 14:12:38 ******/
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
 CONSTRAINT [PK_Page_1] PRIMARY KEY CLUSTERED 
(
	[URL] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[PointsTo]    Script Date: 28/03/2017 14:12:38 ******/
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
/****** Object:  Table [dbo].[UContains]    Script Date: 28/03/2017 14:12:38 ******/
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
/****** Object:  Table [dbo].[Word]    Script Date: 28/03/2017 14:12:38 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[Word](
	[ID] [int] NOT NULL,
	[Text] [varchar](100) NOT NULL,
 CONSTRAINT [PK_Word] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
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
ALTER TABLE [dbo].[UContains]  WITH CHECK ADD  CONSTRAINT [FK_Contains_Word] FOREIGN KEY([WordID])
REFERENCES [dbo].[Word] ([ID])
GO
ALTER TABLE [dbo].[UContains] CHECK CONSTRAINT [FK_Contains_Word]
GO
/****** Object:  StoredProcedure [dbo].[findPage]    Script Date: 28/03/2017 14:12:38 ******/
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
/****** Object:  StoredProcedure [dbo].[getPageAge]    Script Date: 28/03/2017 14:12:38 ******/
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
/****** Object:  StoredProcedure [dbo].[incrementRank]    Script Date: 28/03/2017 14:12:38 ******/
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
/****** Object:  StoredProcedure [dbo].[insertPage]    Script Date: 28/03/2017 14:12:38 ******/
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
/****** Object:  StoredProcedure [dbo].[insertPointsTo]    Script Date: 28/03/2017 14:12:38 ******/
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
/****** Object:  StoredProcedure [dbo].[isoldPage]    Script Date: 28/03/2017 14:12:38 ******/
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
/****** Object:  StoredProcedure [dbo].[tryInsertPage]    Script Date: 28/03/2017 14:12:38 ******/
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
/****** Object:  StoredProcedure [dbo].[updatePage]    Script Date: 28/03/2017 14:12:38 ******/
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
USE [master]
GO
ALTER DATABASE [SearchEngine] SET  READ_WRITE 
GO
